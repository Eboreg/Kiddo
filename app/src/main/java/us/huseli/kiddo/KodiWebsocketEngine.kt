package us.huseli.kiddo

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_WEBSOCKET_PORT
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.ApplicationOnVolumeChanged
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayer
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerId
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerSpeed
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import javax.inject.Inject
import javax.inject.Singleton

interface KodiNotificationListener {
    fun onKodiNotification(notification: Notification<*>)
}

@Singleton
class KodiWebsocketEngine @Inject constructor(@ApplicationContext context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, AbstractScopeHolder(), ILogger, KodiNotificationListener {
    private val listeners = mutableSetOf<KodiNotificationListener>()
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val _applicationOnVolumeChanged = MutableStateFlow<ApplicationOnVolumeChanged?>(null)
    private val _connectError = MutableStateFlow<KodiError?>(null)
    private val _hostname = MutableStateFlow<String?>(preferences.getString(PREF_KODI_HOST, null))
    private val _port = MutableStateFlow<Int>(preferences.getInt(PREF_KODI_WEBSOCKET_PORT, 9090))
    private val _url = combine(_hostname, _port) { hostname, port ->
        hostname?.let { "ws://$it:$port/jsonrpc" }
    }
    private val _playerId = MutableStateFlow<Int?>(null)
    private val _playerSpeed = MutableStateFlow<Int?>(null)

    val connectError = _connectError.asStateFlow()
    val connectErrorString = _connectError.map { it?.message ?: it?.toString() }.distinctUntilChanged()
    val port = _port.asStateFlow()
    val playerId = _playerId.asStateFlow()
    val playerSpeed = _playerSpeed.asStateFlow()
    val isMuted = _applicationOnVolumeChanged.map { it?.muted }
    val volume = _applicationOnVolumeChanged.map { it?.volume }

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)
        addListener(this)

        launchOnMainThread {
            _url.filterNotNull().collectLatest { url ->
                while (true) {
                    val client = HttpClient(CIO) { install(WebSockets) }

                    try {
                        client.webSocket(url) {
                            _connectError.value = null

                            while (isActive) {
                                val frame = try {
                                    (incoming.receiveCatching().getOrNull() as? Frame.Text) ?: break
                                    // incoming.receive() as Frame.Text
                                } catch (e: Throwable) {
                                    throw KodiWebsocketReceiveError(url = url, cause = e)
                                }

                                Notification.fromJson(frame.readText())?.also { notification ->
                                    listeners.forEach { it.onKodiNotification(notification) }
                                }
                            }
                        }
                    } catch (e: Throwable) {
                        val error =
                            if (e !is KodiError) KodiConnectionError(url = url, cause = e)
                            else e

                        logError("client.webSocket()", error)
                        if (error !is KodiWebsocketReceiveError) {
                            _connectError.value = error
                            delay(3000)
                        }
                    } finally {
                        client.close()
                    }
                }
            }
        }
    }

    fun addListener(listener: KodiNotificationListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: KodiNotificationListener) {
        listeners.remove(listener)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_KODI_HOST -> preferences.getString(key, null)?.also { _hostname.value = it }
            PREF_KODI_WEBSOCKET_PORT -> _port.value = preferences.getInt(key, 9090)
        }
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is IHasPlayer) {
            _playerId.value = notification.data.player.playerid
            notification.data.player.speed?.also { _playerSpeed.value = it }
        }
        if (notification.data is IHasPlayerSpeed) {
            notification.data.speed?.also { _playerSpeed.value = it }
        }
        if (notification.data is IHasPlayerId) {
            _playerId.value = notification.data.playerid
        }
        if (notification.data is ApplicationOnVolumeChanged) {
            _applicationOnVolumeChanged.value = notification.data
        }
    }
}
