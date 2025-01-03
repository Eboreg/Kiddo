package us.huseli.kiddo

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_WEBSOCKET_PORT
import us.huseli.kiddo.dataclasses.notifications.KodiNotification
import us.huseli.kiddo.dataclasses.notifications.data.ApplicationOnVolumeChanged
import us.huseli.kiddo.interfaces.IHasPlayer
import us.huseli.kiddo.interfaces.IHasPlayerSpeed
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

interface KodiNotificationListener {
    fun onNotification(notification: KodiNotification<*>)
}

@Singleton
class KodiWebsocketEngine @Inject constructor(@ApplicationContext context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, AbstractScopeHolder(), ILogger, KodiNotificationListener {
    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(KodiNotification::class.java, TypeAdapter())
        .create()
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val _applicationOnVolumeChanged = MutableStateFlow<ApplicationOnVolumeChanged?>(null)
    private val _host = MutableStateFlow<String?>(preferences.getString(PREF_KODI_HOST, null))
    private val _port = MutableStateFlow<Int>(preferences.getInt(PREF_KODI_WEBSOCKET_PORT, 9090))
    private val _url = combine(_host, _port) { host, port ->
        host?.let { "ws://$it:$port/jsonrpc?kodi=" }
    }
    private val _listeners = mutableSetOf<KodiNotificationListener>()
    private val _playerId = MutableStateFlow<Int?>(null)
    private val _playerSpeed = MutableStateFlow<Double?>(null)

    val port = _port.asStateFlow()
    val playerId = _playerId.asStateFlow()
    val playerSpeed = _playerSpeed.filterNotNull()
    val isMuted = _applicationOnVolumeChanged.map { it?.muted }.filterNotNull()
    val volume = _applicationOnVolumeChanged.map { it?.volume }.filterNotNull()

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)
        addListener(this)

        launchOnMainThread {
            _url.collect { url ->
                if (url != null) launchOnMainThread {
                    while (true) {
                        val client = HttpClient(CIO) { install(WebSockets) }

                        try {
                            client.webSocket(url) {
                                while (true) {
                                    val frame = incoming.receive() as Frame.Text
                                    val text = frame.readText()

                                    try {
                                        gson.fromJson(text, KodiNotification::class.java)?.also { notification ->
                                            log("gson.fromJson: text=$text")
                                            log("gson.fromJson: notification=$notification")
                                            _listeners.forEach { it.onNotification(notification) }
                                        }
                                    } catch (e: Throwable) {
                                        logError("gson.fromJson: text=$text", e)
                                    }
                                }
                            }
                        } catch (e: Throwable) {
                            logError("client.webSocket()", e)
                            delay(3000)
                        }
                    }
                }
            }
        }
    }

    fun addListener(listener: KodiNotificationListener) {
        _listeners.add(listener)
    }

    fun removeListener(listener: KodiNotificationListener) {
        _listeners.remove(listener)
    }

    fun setPort(value: Int) {
        preferences.edit().putInt(PREF_KODI_WEBSOCKET_PORT, value).apply()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_KODI_HOST -> preferences.getString(key, null)?.also { _host.value = it }
            PREF_KODI_WEBSOCKET_PORT -> preferences.getInt(key, 0).takeIf { it > 0 }?.also { _port.value = it }
        }
    }

    override fun onNotification(notification: KodiNotification<*>) {
        if (notification.params.data is IHasPlayer) {
            _playerId.value = notification.params.data.player.playerid
            notification.params.data.player.speed?.also { _playerSpeed.value = it }
        }
        if (notification.params.data is IHasPlayerSpeed) {
            notification.params.data.speed?.also { _playerSpeed.value = it }
        }

        if (notification.params.data is ApplicationOnVolumeChanged) {
            _applicationOnVolumeChanged.value = notification.params.data
        }
    }

    inner class TypeAdapter : JsonDeserializer<KodiNotification<*>> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?,
        ): KodiNotification<*>? {
            return json?.asJsonObject?.let { KodiNotification.fromJson(it, gson) }
        }
    }
}
