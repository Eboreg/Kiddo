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
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import javax.inject.Inject
import javax.inject.Singleton

interface KodiNotificationListener {
    fun onKodiNotification(notification: Notification<*>)
}

@Singleton
class KodiWebsocketEngine @Inject constructor(@ApplicationContext context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, AbstractScopeHolder(), ILogger {
    enum class Status { Pending, Connecting, Connected, NoHostname, Error }

    private val listeners = mutableSetOf<KodiNotificationListener>()
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val _connectError = MutableStateFlow<KodiError?>(null)
    private val _hostname = MutableStateFlow<String?>(preferences.getString(PREF_KODI_HOST, null))
    private val _port = MutableStateFlow<Int>(preferences.getInt(PREF_KODI_WEBSOCKET_PORT, 9090))
    private val _url = combine(_hostname, _port) { hostname, port ->
        hostname?.let { "ws://$it:$port/jsonrpc" }
    }
    private val _consecutiveErrorCount = MutableStateFlow(0)
    private val _status = MutableStateFlow(Status.Pending)

    val connectError = _connectError.asStateFlow()
    @Suppress("DEPRECATION") val connectErrorString = _connectError.map { it?.toString() }.distinctUntilChanged()
    val port = _port.asStateFlow()
    val status = _status.asStateFlow()

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)

        launchOnIOThread {
            if (_hostname.value == null) _status.value = Status.NoHostname

            _url.filterNotNull().collectLatest { url ->
                while (true) {
                    val client = HttpClient(CIO) { install(WebSockets) }

                    _status.value = Status.Connecting
                    try {
                        client.webSocket(url) {
                            _connectError.value = null
                            _status.value = Status.Connected

                            while (isActive) {
                                val frame = try {
                                    (incoming.receiveCatching().getOrNull() as? Frame.Text) ?: break
                                } catch (e: Throwable) {
                                    throw KodiWebsocketReceiveError(url, e)
                                }

                                Notification.fromJson(frame.readText())?.also { notification ->
                                    listeners.forEach { it.onKodiNotification(notification) }
                                }
                                _consecutiveErrorCount.value = 0
                            }
                        }
                    } catch (e: Throwable) {
                        val error =
                            if (e !is KodiError) KodiConnectionError(url, e)
                            else e

                        _status.value = Status.Error
                        _consecutiveErrorCount.value++
                        logError("client.webSocket()", error)
                        if (error !is KodiWebsocketReceiveError) {
                            if (_consecutiveErrorCount.value >= 3) _connectError.value = error
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
}
