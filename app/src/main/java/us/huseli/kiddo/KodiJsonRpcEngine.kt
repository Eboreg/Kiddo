package us.huseli.kiddo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.preference.PreferenceManager
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_PORT
import us.huseli.kiddo.dataclasses.responses.KodiJsonRpcResponse
import us.huseli.kiddo.dataclasses.responses.result.ApplicationGetPropertiesResult
import us.huseli.kiddo.dataclasses.responses.result.GetActivePlayersResult
import us.huseli.kiddo.dataclasses.responses.result.PlayerGetItemResult
import us.huseli.kiddo.dataclasses.responses.result.PlayerGetPropertiesResult
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import java.lang.reflect.Type
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KodiJsonRpcEngine @Inject constructor(@ApplicationContext context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, AbstractScopeHolder(), ILogger {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val _host = MutableStateFlow<String?>(preferences.getString(PREF_KODI_HOST, null))
    private val _port = MutableStateFlow<Int>(preferences.getInt(PREF_KODI_PORT, 80))
    private val _volume = MutableStateFlow<Int?>(null)
    private val _isMuted = MutableStateFlow<Boolean?>(null)
    private val _playerGetItemResult = MutableStateFlow<PlayerGetItemResult?>(null)
    private val _playerGetPropertiesResult = MutableStateFlow<PlayerGetPropertiesResult?>(null)
    private var _requestId = 1

    val host = _host.asStateFlow()
    val port = _port.asStateFlow()
    val volume = _volume.filterNotNull()
    val playerSpeed = _playerGetPropertiesResult.map { it?.speed }.filterNotNull()
    val playerTotalTime = _playerGetPropertiesResult.map { it?.totaltime?.duration }
    val playerElapsedTime = _playerGetPropertiesResult.map { it?.time?.duration }
    val isMuted = _isMuted.filterNotNull()
    val playerThumbnailImage = _playerGetItemResult.map { result ->
        result?.item?.thumbnail?.takeIf { it.isNotEmpty() }?.let { path -> getImage(path) }
    }
    val playerTitle = _playerGetItemResult.map { it?.item?.title ?: it?.item?.label }

    init {
        preferences.registerOnSharedPreferenceChangeListener(this)

        launchOnIOThread {
            fetchApplicationProperties()
            fetchActivePlayers()
        }
    }

    suspend fun fetchPlayerItem(playerId: Int) {
        getPlayerItem(playerId)?.also { response ->
            _playerGetItemResult.value = response.firstOrNull()?.result
        }
    }

    suspend fun fetchPlayerProperties(playerId: Int) {
        getPlayerProperties(playerId)?.firstOrNull()?.result?.also {
            _playerGetPropertiesResult.value = it
        }
    }

    suspend fun playOrPause(playerId: Int) = post<Any>("Player.PlayPause", listOf(playerId))

    suspend fun seekPercentage(playerId: Int, value: Float) = post<Any>(
        "Player.Seek",
        listOf(playerId, mapOf("percentage" to value)),
    )

    suspend fun sendKeypress(key: String) = post<Any>("Input.$key")

    suspend fun setFullScreen(value: Boolean) = post<Boolean>("GUI.SetFullScreen", listOf(value))

    fun setHost(value: String) {
        preferences.edit().putString(PREF_KODI_HOST, value).apply()
    }

    suspend fun setMute(value: Boolean) = post<Boolean>("Application.SetMute", listOf(value))

    fun setPort(value: Int) {
        preferences.edit().putInt(PREF_KODI_PORT, value).apply()
    }

    suspend fun setVolume(value: Int) = post<Int>("Application.SetVolume", listOf(value))?.also { response ->
        response.firstOrNull()?.result?.also { _volume.value = it }
    }

    suspend fun stop(playerId: Int) = post<Any>("Player.Stop", listOf(playerId))

    /** PRIVATE METHODS ***********************************************************************************************/

    private suspend fun fetchActivePlayers() {
        getActivePlayers()
            ?.mapNotNull { it.result }
            ?.flatten()
            ?.firstOrNull()
            ?.also {
                fetchPlayerProperties(it.playerid)
                fetchPlayerItem(it.playerid)
            }
    }

    private suspend fun fetchApplicationProperties() {
        getApplicationProperties()?.also { response ->
            response.firstOrNull()?.result?.also { result ->
                _volume.value = result.volume
                _isMuted.value = result.muted
            }
        }
    }

    private suspend fun getActivePlayers(): List<KodiJsonRpcResponse<List<GetActivePlayersResult>>>? {
        val typeToken = TypeToken.getParameterized(ArrayList::class.java, GetActivePlayersResult::class.java)

        return post<List<GetActivePlayersResult>>(typeToken.type, "Player.GetActivePlayers")
    }

    private suspend fun getApplicationProperties() = post<ApplicationGetPropertiesResult>(
        "Application.GetProperties",
        listOf(listOf("volume", "muted", "name", "version", "sorttokens", "language")),
    )

    private suspend fun getImage(path: String): Bitmap? {
        return _host.value?.let { host ->
            val url = "http://$host:${_port.value}/image/${URLEncoder.encode(path, "UTF-8")}"
            Request(url = url).getBitmap()
        }
    }

    private suspend fun getPlayerItem(playerId: Int) = post<PlayerGetItemResult>(
        "Player.GetItem",
        listOf(playerId, listOf("title", "fanart", "thumbnail", "channel")),
    )

    private suspend fun getPlayerProperties(playerId: Int) = post<PlayerGetPropertiesResult>(
        "Player.GetProperties",
        listOf(
            playerId,
            listOf(
                "audiostreams",
                "canchangespeed",
                "canmove",
                "canrepeat",
                "canseek",
                "canshuffle",
                "currentaudiostream",
                "currentsubtitle",
                "currentvideostream",
                "repeat",
                "shuffled",
                "speed",
                "subtitleenabled",
                "subtitles",
                "time",
                "totaltime",
                "type",
                "videostreams",
            ),
        ),
    )

    private suspend fun <Result> post(
        resultType: Type,
        method: String,
        params: List<Any> = emptyList(),
    ): List<KodiJsonRpcResponse<Result>>? {
        val responseTypeToken = TypeToken.getParameterized(KodiJsonRpcResponse::class.java, resultType)
        val typeToken = TypeToken.getParameterized(ArrayList::class.java, responseTypeToken.type)
        val type = typeToken.type

        return _host.value?.let { host ->
            val url = "http://$host:${_port.value}/jsonrpc"
            val json = listOf(
                mapOf(
                    "jsonrpc" to "2.0",
                    "method" to method,
                    "params" to params,
                    "id" to ++_requestId,
                )
            )

            try {
                Request.postJson(url = url, json = json).getObject<List<KodiJsonRpcResponse<Result>>>(type).also {
                    log("post: method=$method, response=$it")
                }
            } catch (e: Throwable) {
                logError(e.message, e)
                null
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private suspend inline fun <reified Result> post(
        method: String,
        params: List<Any> = emptyList(),
    ): List<KodiJsonRpcResponse<Result>>? =
        post<Result>(resultType = Result::class.java, method = method, params = params)

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_KODI_HOST -> preferences.getString(key, null)?.also { _host.value = it }
            PREF_KODI_PORT -> preferences.getInt(key, 0).takeIf { it > 0 }?.also { _port.value = it }
        }
    }
}
