package us.huseli.kiddo

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
import us.huseli.kiddo.data.KodiJsonRpcResponse
import us.huseli.kiddo.data.PlaylistWithItems
import us.huseli.kiddo.data.enums.ApplicationPropertyName
import us.huseli.kiddo.data.enums.GuiPropertyName
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.enums.PlayerPropertyName
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.requests.ApplicationGetProperties
import us.huseli.kiddo.data.requests.GuiGetProperties
import us.huseli.kiddo.data.requests.PlayerGetActivePlayers
import us.huseli.kiddo.data.requests.PlayerGetItem
import us.huseli.kiddo.data.requests.PlayerGetProperties
import us.huseli.kiddo.data.requests.PlayerSeek
import us.huseli.kiddo.data.requests.PlayerSetSubtitle
import us.huseli.kiddo.data.requests.PlaylistGetItems
import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.kiddo.data.requests.interfaces.IRequest
import us.huseli.kiddo.data.types.GuiPropertyValue
import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

interface KodiResponseListener {
    fun onKodiResponse(response: KodiJsonRpcResponse<*>)
}

@OptIn(ExperimentalEncodingApi::class)
@Singleton
class KodiJsonRpcEngine @Inject constructor(@ApplicationContext context: Context) :
    SharedPreferences.OnSharedPreferenceChangeListener, AbstractScopeHolder(), ILogger, KodiResponseListener {
    private val listeners = mutableSetOf<KodiResponseListener>()
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private var requestId = 1

    private val _activePlayer = MutableStateFlow<PlayerGetActivePlayers.ResultItem?>(null)
    private val _connectError = MutableStateFlow<KodiError?>(null)
    private val _hostname = MutableStateFlow<String?>(preferences.getString(PREF_KODI_HOST, null))
    private val _isFullscreen = MutableStateFlow<Boolean>(false)
    private val _isMuted = MutableStateFlow<Boolean?>(null)
    private val _password = MutableStateFlow<String?>(preferences.getString(PREF_KODI_PASSWORD, null))
    private val _playerElapsedTime = MutableStateFlow(0L)
    private val _playerItem = MutableStateFlow<ListItemAll?>(null)
    private val _playerProperties = MutableStateFlow<PlayerPropertyValue?>(null)
    private val _playerSpeed = MutableStateFlow<Int?>(null)
    private val _playerTotalTime = MutableStateFlow<Long?>(null)
    private val _playlists =
        MutableStateFlow<Map<PlaylistType, PlaylistWithItems>>(emptyMap())
    private val _port = MutableStateFlow<Int>(preferences.getInt(PREF_KODI_PORT, 80))
    private val _username = MutableStateFlow<String?>(preferences.getString(PREF_KODI_USERNAME, null))
    private val _volume = MutableStateFlow<Int?>(null)

    private val _url = combine(_hostname, _port) { hostname, port ->
        hostname?.let { "http://$hostname:$port/jsonrpc" }
    }

    val activePlayer = _activePlayer.asStateFlow()
    val authToken = combine(
        _username.map { it?.takeIfNotBlank() },
        _password.map { it?.takeIfNotBlank() },
    ) { username, password ->
        if (username != null && password != null) Base64.encode("$username:$password".encodeToByteArray())
        else null
    }
    val connectError = _connectError.asStateFlow()
    val connectErrorString = _connectError.map { it?.message ?: it?.toString() }.distinctUntilChanged()
    val hostname = _hostname.asStateFlow()
    val isFullscreen = _isFullscreen.asStateFlow()
    val isMuted = _isMuted.asStateFlow()
    val password = _password.asStateFlow()
    val playerElapsedTime = _playerProperties.map { it?.time?.totalMilliseconds }.filterNotNull()
    val playerItem = _playerItem.asStateFlow()
    val playerProperties = _playerProperties.asStateFlow()
    val playerSpeed = _playerSpeed.asStateFlow()
    val playerTotalTime = _playerTotalTime.asStateFlow()
    val playlists: StateFlow<Map<PlaylistType, PlaylistWithItems>> = _playlists.asStateFlow()
    val port = _port.asStateFlow()
    val username = _username.asStateFlow()
    val volume = _volume.asStateFlow()

    init {
        addListener(this)
        preferences.registerOnSharedPreferenceChangeListener(this)
        initialize()
    }

    fun addListener(listener: KodiResponseListener) {
        listeners.add(listener)
    }

    suspend fun fetchPlayerItem(playerId: Int) {
        post(
            PlayerGetItem(
                playerId = playerId,
                properties = listOf(
                    ListFieldsAll.Artist,
                    ListFieldsAll.CustomProperties,
                    ListFieldsAll.Director,
                    ListFieldsAll.Duration,
                    ListFieldsAll.Fanart,
                    ListFieldsAll.File,
                    ListFieldsAll.Runtime,
                    ListFieldsAll.Thumbnail,
                    ListFieldsAll.Title,
                ),
            ),
        )
    }

    suspend fun fetchPlayerProperties(playerId: Int) {
        post(
            PlayerGetProperties(
                playerId = playerId,
                properties = listOf(
                    PlayerPropertyName.AudioStreams,
                    PlayerPropertyName.CanChangeSpeed,
                    PlayerPropertyName.CanMove,
                    PlayerPropertyName.CanRepeat,
                    PlayerPropertyName.CanSeek,
                    PlayerPropertyName.CanShuffle,
                    PlayerPropertyName.CurrentAudioStream,
                    PlayerPropertyName.CurrentSubtitle,
                    PlayerPropertyName.Repeat,
                    PlayerPropertyName.Shuffled,
                    PlayerPropertyName.Speed,
                    PlayerPropertyName.SubtitleEnabled,
                    PlayerPropertyName.Subtitles,
                    PlayerPropertyName.Time,
                    PlayerPropertyName.TotalTime,
                    PlayerPropertyName.Type,
                ),
            ),
        )
    }

    suspend fun fetchPlaylists() {
        post(PlaylistGetPlaylists())?.also { playlists ->
            for (playlist in playlists) {
                _playlists.value += playlist.type to PlaylistWithItems(
                    playlist = playlist,
                    items = listPlaylistItems(playlistId = playlist.playlistid),
                )
            }
        }
    }

    suspend fun getGuiProperties() = post(GuiGetProperties(properties = listOf(GuiPropertyName.Fullscreen)))

    fun initialize() {
        launchOnIOThread { fetchApplicationProperties() }
        launchOnIOThread { fetchActivePlayer() }
        launchOnIOThread { getGuiProperties() }
    }

    suspend fun listPlaylistItems(playlistId: Int): List<ListItemAll> {
        return post(
            PlaylistGetItems(
                playlistId = playlistId,
                properties = listOf(
                    ListFieldsAll.Artist,
                    ListFieldsAll.CustomProperties,
                    ListFieldsAll.Director,
                    ListFieldsAll.Duration,
                    ListFieldsAll.File,
                    ListFieldsAll.Runtime,
                    ListFieldsAll.Thumbnail,
                    ListFieldsAll.Title,
                ),
            )
        )?.items ?: emptyList()
    }

    suspend fun <Result> post(request: IRequest<Result>): Result? {
        val url = _url.filterNotNull().first()
        val headers = authToken.first()?.let { mapOf("Authorization" to "Basic $it") } ?: emptyMap()

        return try {
            val response = request.post(url = url, requestId = ++requestId, headers = headers).first()

            listeners.forEach { it.onKodiResponse(response) }
            response.result
        } catch (e: Throwable) {
            val error =
                if (e is KodiError) e
                else KodiConnectionError(url = url, cause = e)

            logError("post()", error)
            _connectError.value = error
            null
        }
    }

    @Suppress("unused")
    fun removeListener(listener: KodiResponseListener) {
        listeners.remove(listener)
    }

    private suspend fun fetchActivePlayer() {
        post(PlayerGetActivePlayers())?.firstOrNull().also {
            _activePlayer.value = it
        }
    }

    private suspend fun fetchApplicationProperties() {
        post(
            ApplicationGetProperties(
                properties = listOf(
                    ApplicationPropertyName.Name,
                    ApplicationPropertyName.Volume,
                    ApplicationPropertyName.Muted,
                    ApplicationPropertyName.Version,
                    ApplicationPropertyName.Language,
                ),
            ),
        )
    }

    override fun onKodiResponse(response: KodiJsonRpcResponse<*>) {
        if (response.result is PlayerSeek.Result) {
            response.result.time?.totalMilliseconds?.also { _playerElapsedTime.value = it }
            response.result.totaltime?.totalMilliseconds?.also { _playerTotalTime.value = it }
        }
        if (response.result is IHasPlayerSpeed) {
            response.result.speed?.also { _playerSpeed.value = it }
        }
        if (response.method == "Application.SetVolume" && response.result is Int) {
            _volume.value = response.result
        }
        if (response.method == "GUI.SetFullscreen" && response.result is Boolean) {
            _isFullscreen.value = response.result
        }
        if (response.result is GuiPropertyValue) {
            response.result.fullscreen?.also { _isFullscreen.value = it }
        }
        if (response.result is PlayerPropertyValue) {
            log("onKodiResponse: response.result=${response.result}")
            _playerProperties.value = response.result
        }
        if (response.result is PlayerGetItem.Result) {
            _playerItem.value = response.result.item
        }
        if (response.result is ApplicationGetProperties.Result) {
            _volume.value = response.result.volume
            _isMuted.value = response.result.muted
        }
        if (response.request is PlayerSetSubtitle && !response.isError) {
            launchOnIOThread {
                // Seems like there is some lag between setting subtitles and player properties being correctly
                // updated, hence the ugly little arbitrary delay:
                delay(500)
                fetchPlayerProperties(playerId = response.request.playerId)
            }
        }
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
