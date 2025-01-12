package us.huseli.kiddo

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_PASSWORD
import us.huseli.kiddo.Constants.PREF_KODI_PORT
import us.huseli.kiddo.Constants.PREF_KODI_USERNAME
import us.huseli.kiddo.data.AbstractRequest
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface KodiResponseListener {
    fun <Result : Any> onKodiRequestSucceeded(request: AbstractRequest<Result>, result: Result)
    fun onKodiRequestError(error: KodiError) {}
}

@OptIn(ExperimentalEncodingApi::class)
@Singleton
class KodiJsonRpcEngine @Inject constructor(@ApplicationContext context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, AbstractScopeHolder(), ILogger {
    private val listeners = mutableSetOf<KodiResponseListener>()
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val rateLimitedRequestManager = RateLimitedRequestManager()
    private var requestId = 1

    private val _connectError = MutableStateFlow<KodiError?>(null)
    private val _hostname = MutableStateFlow<String?>(preferences.getString(PREF_KODI_HOST, null))
    private val _password = MutableStateFlow<String?>(preferences.getString(PREF_KODI_PASSWORD, null))
    private val _port = MutableStateFlow<Int>(preferences.getInt(PREF_KODI_PORT, 80))
    private val _username = MutableStateFlow<String?>(preferences.getString(PREF_KODI_USERNAME, null))

    private val _url = combine(_hostname, _port) { hostname, port ->
        hostname?.let { "http://$hostname:$port/jsonrpc" }
    }

    val authToken = combine(
        _username.map { it?.takeIfNotBlank() },
        _password.map { it?.takeIfNotBlank() },
    ) { username, password ->
        if (username != null && password != null) Base64.encode("$username:$password".encodeToByteArray())
        else null
    }
    val connectError = _connectError.asStateFlow()
    @Suppress("DEPRECATION") val connectErrorString =
        _connectError.map { it?.message ?: it?.toString() }.distinctUntilChanged()
    val hostname = _hostname.asStateFlow()
    val password = _password.asStateFlow()
    val port = _port.asStateFlow()
    val username = _username.asStateFlow()

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    suspend fun <Result : Any, Request : AbstractRequest<Result>> post(request: Request): Request {
        rateLimitedRequestManager.cancelPending(request.method)
        return post(request, ++requestId)
    }

    suspend fun <Result : Any, Request : AbstractRequest<Result>> postRateLimited(
        request: Request,
        minIntervalMs: Long,
    ) {
        val requestId = ++this.requestId

        rateLimitedRequestManager.run(
            request = request,
            requestId = requestId,
            minIntervalMs = minIntervalMs,
        ) { post(request, requestId) }
    }

    fun registerListener(listener: KodiResponseListener) {
        listeners.add(listener)
    }

    @Suppress("unused")
    fun unregisterListener(listener: KodiResponseListener) {
        listeners.remove(listener)
    }

    private suspend fun getHeaders(): Map<String, String> =
        authToken.first()?.let { mapOf("Authorization" to "Basic $it") } ?: emptyMap()

    private suspend fun <Result : Any, Request : AbstractRequest<Result>> post(
        request: Request,
        requestId: Int,
    ): Request {
        val url = _url.filterNotNull().first()

        try {
            request.post(url = url, requestId = requestId, headers = getHeaders())
            request.resultOrNull?.also { result ->
                listeners.forEach { it.onKodiRequestSucceeded(request, result) }
            }
            _connectError.value = null
        } catch (e: Throwable) {
            val error = when (e) {
                is KodiError -> e
                is SocketTimeoutException -> KodiConnectionTimeoutError(url, e)
                else -> KodiConnectionError(url, e)
            }

            listeners.forEach { it.onKodiRequestError(error) }
            logError("post()", error)
            _connectError.value = error
        }

        return request
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_KODI_HOST -> preferences.getString(key, null)?.also { _hostname.value = it }
            PREF_KODI_PORT -> _port.value = preferences.getInt(key, 80)
            PREF_KODI_USERNAME -> _username.value = preferences.getString(key, null)?.takeIfNotBlank()
            PREF_KODI_PASSWORD -> _password.value = preferences.getString(key, null)?.takeIfNotBlank()
        }
    }
}
