package us.huseli.kiddo

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_PASSWORD
import us.huseli.kiddo.Constants.PREF_KODI_PORT
import us.huseli.kiddo.Constants.PREF_KODI_USERNAME
import us.huseli.kiddo.Constants.PREF_KODI_WEBSOCKET_PORT
import us.huseli.kiddo.data.PlaylistWithItems
import us.huseli.kiddo.data.enums.AudioFieldsAlbum
import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.enums.GuiWindow
import us.huseli.kiddo.data.enums.InputAction
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.PlayerOnSeek
import us.huseli.kiddo.data.notifications.data.PlayerOnStop
import us.huseli.kiddo.data.requests.ApplicationSetMute
import us.huseli.kiddo.data.requests.ApplicationSetVolume
import us.huseli.kiddo.data.requests.AudioLibraryGetAlbums
import us.huseli.kiddo.data.requests.GuiActivateWindow
import us.huseli.kiddo.data.requests.GuiSetFullscreen
import us.huseli.kiddo.data.requests.InputExecuteAction
import us.huseli.kiddo.data.requests.InputKeyPress
import us.huseli.kiddo.data.requests.InputSendText
import us.huseli.kiddo.data.requests.PlayerGoTo
import us.huseli.kiddo.data.requests.PlayerOpen
import us.huseli.kiddo.data.requests.PlayerPlayPause
import us.huseli.kiddo.data.requests.PlayerSeek
import us.huseli.kiddo.data.requests.PlayerSetAudioStream
import us.huseli.kiddo.data.requests.PlayerSetSpeed
import us.huseli.kiddo.data.requests.PlayerSetSubtitle
import us.huseli.kiddo.data.requests.PlayerSetTempo
import us.huseli.kiddo.data.requests.PlayerStop
import us.huseli.kiddo.data.requests.PlaylistAdd
import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.kiddo.data.requests.PlaylistRemove
import us.huseli.kiddo.data.requests.PlaylistSwap
import us.huseli.kiddo.data.requests.VideoLibraryGetMovieDetails
import us.huseli.kiddo.data.requests.VideoLibraryGetMovies
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.ListFilterAlbums
import us.huseli.kiddo.data.types.ListFilterMovies
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.data.uistates.PlayerItemUiState
import us.huseli.retaintheme.RetainConnectionError
import us.huseli.retaintheme.RetainHttpError
import us.huseli.retaintheme.request.Request
import us.huseli.retaintheme.snackbar.SnackbarEngine
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val jsonEngine: KodiJsonRpcEngine,
    private val websocketEngine: KodiWebsocketEngine,
    @ApplicationContext private val context: Context,
) : KodiNotificationListener, AbstractScopeHolder(), ILogger {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val _isSeeking = MutableStateFlow(false)
    private val _movieDetails = MutableStateFlow<Map<Int, VideoDetailsMovie>>(emptyMap())
    private val _playerElapsedTime = MutableStateFlow(0L)
    private val _playerId = merge(
        websocketEngine.playerId.filterNotNull(),
        jsonEngine.activePlayer.map { it?.playerid }.filterNotNull(),
    )
    private val _playerProgress = MutableStateFlow(0f)
    private val _playerSpeed = merge(
        websocketEngine.playerSpeed.filterNotNull(),
        jsonEngine.playerProperties.map { it?.speed }.filterNotNull(),
        jsonEngine.playerSpeed.filterNotNull(),
    )
    private val _playerTotalTime = merge(
        jsonEngine.playerProperties.map { it?.totaltime?.totalMilliseconds }.filterNotNull().filter { it > 0 },
        jsonEngine.playerTotalTime.filterNotNull().filter { it > 0 },
        jsonEngine.playerItem
            .map { (it?.duration ?: it?.runtime)?.toLong()?.times(1000) }
            .filterNotNull()
            .filter { it > 0 },
    )
    private val _timestamp = MutableStateFlow<Long>(System.currentTimeMillis())

    val connectErrorStrings = combine(
        websocketEngine.connectError.map { it?.message ?: it?.toString() }.distinctUntilChanged(),
        jsonEngine.connectError.map { it?.message ?: it?.toString() }.distinctUntilChanged(),
    ) { websocket, json ->
        listOfNotNull(websocket, json)
    }.stateWhileSubscribed(emptyList())
    val hostname = jsonEngine.hostname
    val isFullscreen = jsonEngine.isFullscreen
    val isMuted = merge(
        jsonEngine.isMuted.filterNotNull(),
        websocketEngine.isMuted.filterNotNull(),
    ).stateWhileSubscribed(false)
    val isPlaying = _playerSpeed.map { it == 1 }.stateWhileSubscribed(false)
    val jsonPort = jsonEngine.port
    val password = jsonEngine.password
    val playerCoverImage = jsonEngine.playerItem.map {
        val path = it?.art?.poster?.takeIfNotBlank()
            ?: it?.art?.fanart?.takeIfNotBlank()
            ?: it?.fanart?.takeIfNotBlank()

        path?.let { getImageBitmap(path) }
    }.stateWhileSubscribed()
    val playerElapsedTimeSeconds =
        _playerElapsedTime.map { it.div(1000).toInt() }.distinctUntilChanged().stateWhileSubscribed()
    val playerItem = jsonEngine.playerItem
    val playerItemUiState = combine(jsonEngine.playerItem, jsonEngine.playerProperties) { item, properties ->
        item?.takeIf { it.stringId != null }?.let { PlayerItemUiState(it, properties) }
    }.stateWhileSubscribed()
    val playerProgress = _playerProgress.asStateFlow()
    val playerThumbnailImage: StateFlow<ImageBitmap?> = jsonEngine.playerItem.map {
        it?.thumbnail?.takeIfNotBlank()?.let { path -> getImageBitmap(path) }
    }.stateWhileSubscribed()
    val playerTotalTimeSeconds =
        _playerTotalTime.map { it.div(1000).toInt() }.distinctUntilChanged().stateWhileSubscribed()
    val playlists: StateFlow<Map<PlaylistType, PlaylistWithItems>> = jsonEngine.playlists
    val username = jsonEngine.username
    val volume = merge(
        jsonEngine.volume.filterNotNull(),
        websocketEngine.volume.filterNotNull(),
    ).stateWhileSubscribed()
    val websocketPort = websocketEngine.port

    init {
        websocketEngine.addListener(this)

        launchOnMainThread {
            jsonEngine.connectErrorString.filterNotNull().collect { error ->
                SnackbarEngine.addError(error)
            }
        }

        launchOnMainThread {
            websocketEngine.connectErrorString.filterNotNull().collect { error ->
                SnackbarEngine.addError(error)
            }
        }

        launchOnIOThread {
            combine(jsonEngine.playerElapsedTime, _isSeeking) { elapsed, isSeeking ->
                elapsed.takeIf { !isSeeking }
            }.filterNotNull().collect { elapsed ->
                _playerElapsedTime.value = elapsed
            }
        }

        launchOnIOThread {
            _playerId.distinctUntilChanged().collect { playerId ->
                jsonEngine.fetchPlayerProperties(playerId)
                jsonEngine.fetchPlayerItem(playerId)
            }
        }

        launchOnMainThread {
            combine(
                _playerElapsedTime,
                _playerTotalTime,
                _isSeeking,
            ) { elapsed, time, isSeeking ->
                if (isSeeking) null
                else time
                    .takeIf { it > 0 }
                    ?.let { elapsed.div(it.toFloat()) }
                    ?.takeIf { !it.isNaN() } ?: 0f
            }.filterNotNull().distinctUntilChanged().collect { progress ->
                _playerProgress.value = progress
            }
        }

        launchOnMainThread {
            combine(_playerSpeed, _isSeeking) { speed, isSeeking ->
                speed.takeIf { !isSeeking }
            }.collectLatest { speed ->
                if (speed != null) {
                    while (true) {
                        val timestamp = System.currentTimeMillis()
                        val increment = (timestamp - _timestamp.value).times(speed).toLong()

                        _playerElapsedTime.value += increment
                        _timestamp.value = timestamp
                        delay(500)
                    }
                }
            }
        }
    }

    fun addNotificationListener(listener: KodiNotificationListener) = websocketEngine.addListener(listener)

    suspend fun decreasePlayerSpeed() =
        jsonEngine.post(PlayerSetSpeed(playerId = _playerId.first(), type = PlayerSetSpeed.ParamType.Decrement))

    @Suppress("unused")
    suspend fun decreasePlayerTempo() =
        jsonEngine.post(PlayerSetTempo(playerId = _playerId.first(), type = PlayerSetTempo.ParamType.Decrement))

    suspend fun decreaseVolume() =
        jsonEngine.post(ApplicationSetVolume(type = ApplicationSetVolume.ParamType.Decrement))

    suspend fun enqueueMovie(playlistId: Int, movieId: Int, title: String): String? {
        val result = jsonEngine.post(PlaylistAdd(playlistId = playlistId, item = PlaylistItem(movieId = movieId)))

        if (result == "OK") SnackbarEngine.addInfo(context.getString(R.string.x_was_enqueued, title))
        return result
    }

    suspend fun executeInputAction(action: InputAction) = jsonEngine.post(InputExecuteAction(action = action))

    suspend fun fetchPlaylists() = jsonEngine.fetchPlaylists()

    suspend fun getImageBitmap(path: String): ImageBitmap? {
        return try {
            jsonEngine.hostname.value?.let { host ->
                val url = "http://$host:${jsonEngine.port.value}/image/${URLEncoder.encode(path, "UTF-8")}"
                Request(url = url).getBitmap()?.asImageBitmap()
            }
        } catch (e: Throwable) {
            val message =
                if (e is RetainHttpError) "${e.url}: $e (HTTP ${e.statusCode})"
                else if (e is RetainConnectionError) "${e.url}: $e"
                else e.message ?: e.toString()

            logError("getImageBitmap($path): $message", e)
            null
        }
    }

    suspend fun getMovieDetails(id: Int): VideoDetailsMovie? {
        _movieDetails.value[id]?.also { return it }

        return jsonEngine.post(
            VideoLibraryGetMovieDetails(
                movieId = id,
                properties = listOf(
                    VideoFieldsMovie.Art,
                    VideoFieldsMovie.Cast,
                    VideoFieldsMovie.Country,
                    VideoFieldsMovie.Director,
                    VideoFieldsMovie.Fanart,
                    VideoFieldsMovie.File,
                    VideoFieldsMovie.Genre,
                    VideoFieldsMovie.OriginalTitle,
                    VideoFieldsMovie.Plot,
                    VideoFieldsMovie.PlotOutline,
                    VideoFieldsMovie.Rating,
                    VideoFieldsMovie.Runtime,
                    VideoFieldsMovie.Tag,
                    VideoFieldsMovie.Tagline,
                    VideoFieldsMovie.Thumbnail,
                    VideoFieldsMovie.Title,
                    VideoFieldsMovie.Votes,
                    VideoFieldsMovie.Year,
                ),
            )
        )?.moviedetails?.also { _movieDetails.value += id to it }
    }

    suspend fun getPlaylists() = jsonEngine.post(PlaylistGetPlaylists())

    suspend fun goToNextItem() = jsonEngine.post(PlayerGoTo(playerId = _playerId.first(), to = PlayerGoTo.To.Next))

    suspend fun goToPreviousItem() =
        jsonEngine.post(PlayerGoTo(playerId = _playerId.first(), to = PlayerGoTo.To.Previous))

    suspend fun increasePlayerSpeed() =
        jsonEngine.post(PlayerSetSpeed(playerId = _playerId.first(), type = PlayerSetSpeed.ParamType.Increment))

    @Suppress("unused")
    suspend fun increasePlayerTempo() =
        jsonEngine.post(PlayerSetTempo(playerId = _playerId.first(), type = PlayerSetTempo.ParamType.Increment))

    suspend fun increaseVolume() =
        jsonEngine.post(ApplicationSetVolume(type = ApplicationSetVolume.ParamType.Increment))

    suspend fun listAlbums(filter: ListFilterAlbums? = null): List<AudioDetailsAlbum>? = jsonEngine.post(
        AudioLibraryGetAlbums(
            properties = listOf(
                AudioFieldsAlbum.AlbumDuration,
                AudioFieldsAlbum.Art,
                AudioFieldsAlbum.Artist,
                AudioFieldsAlbum.Genre,
                AudioFieldsAlbum.Rating,
                AudioFieldsAlbum.Title,
                AudioFieldsAlbum.Year,
            ),
            filter = filter,
            sort = ListSort(method = ListSort.Method.Title),
        )
    )?.albums

    suspend fun listMovies(
        simpleFilter: VideoLibraryGetMovies.SimpleFilter? = null,
        filter: ListFilterMovies? = null,
    ): List<VideoDetailsMovie>? =
        jsonEngine.post(
            VideoLibraryGetMovies(
                properties = listOf(
                    VideoFieldsMovie.Art,
                    VideoFieldsMovie.Director,
                    VideoFieldsMovie.File,
                    VideoFieldsMovie.Genre,
                    VideoFieldsMovie.Rating,
                    VideoFieldsMovie.Runtime,
                    VideoFieldsMovie.Tagline,
                    VideoFieldsMovie.Title,
                    VideoFieldsMovie.Year,
                ),
                simpleFilter = simpleFilter,
                filter = filter,
                sort = ListSort(method = ListSort.Method.Title),
            )
        )?.movies

    suspend fun listPlaylistItems(playlistId: Int) = jsonEngine.listPlaylistItems(playlistId)

    suspend fun openSubtitleSearch() = jsonEngine.post(GuiActivateWindow(window = GuiWindow.SubtitleSearch))

    suspend fun playerOpenPlaylist(playlistId: Int, position: Int? = null) =
        jsonEngine.post(PlayerOpen.Playlist(playlistId = playlistId, position = position))

    suspend fun playMovie(movieId: Int) =
        jsonEngine.post(PlayerOpen.PlaylistItem(item = PlaylistItem(movieId = movieId)))

    suspend fun playOrPause() = jsonEngine.post(PlayerPlayPause(playerId = _playerId.first()))

    fun removeNotificationListener(listener: KodiNotificationListener) = websocketEngine.removeListener(listener)

    suspend fun removePlaylistItem(playlistId: Int, position: Int) =
        jsonEngine.post(PlaylistRemove(playlistId = playlistId, position = position))

    suspend fun seekFinish(progress: Float) {
        setPlayerElapsedTimeFromProgress(progress.toDouble())
        jsonEngine.post(
            PlayerSeek(
                playerId = _playerId.first(),
                value = PlayerSeek.Value(percentage = progress * 100f),
            )
        )
        _isSeeking.value = false
    }

    suspend fun seekInProgress(progress: Float) {
        _isSeeking.value = true
        _playerProgress.value = progress
        setPlayerElapsedTimeFromProgress(progress.toDouble())
    }

    suspend fun sendKeypress(key: String) = jsonEngine.post(InputKeyPress(key))

    suspend fun sendText(text: String, done: Boolean? = null) = jsonEngine.post(InputSendText(text = text, done = done))

    suspend fun setAudioStream(index: Int) =
        jsonEngine.post(PlayerSetAudioStream(playerId = _playerId.first(), streamIndex = index))

    suspend fun setMute(value: Boolean) = jsonEngine.post(ApplicationSetMute(mute = GlobalToggle.fromValue(value)))

    suspend fun disableSubtitle() = jsonEngine.post(
        PlayerSetSubtitle(
            playerId = _playerId.first(),
            subtitle = PlayerSetSubtitle.Subtitle.Off,
            enable = false,
        )
    )

    suspend fun setSubtitle(index: Int) =
        jsonEngine.post(PlayerSetSubtitle(playerId = _playerId.first(), subtitleIndex = index, enable = true))

    suspend fun setVolume(value: Int) = jsonEngine.post(ApplicationSetVolume(volume = value))

    suspend fun stop() = jsonEngine.post(PlayerStop(playerId = _playerId.first()))

    suspend fun swapPlaylistPositions(playlistId: Int, position1: Int, position2: Int) =
        jsonEngine.post(PlaylistSwap(playlistId = playlistId, position1 = position1, position2 = position2))

    suspend fun toggleFullscreen() = jsonEngine.post(GuiSetFullscreen(fullscreen = GlobalToggle.Toggle))

    fun updateSettings(hostname: String, username: String, password: String, jsonPort: String, websocketPort: String) {
        preferences.edit()
            .putString(PREF_KODI_HOST, hostname)
            .putString(PREF_KODI_USERNAME, username.takeIfNotBlank())
            .putString(PREF_KODI_PASSWORD, password.takeIfNotBlank())
            .apply {
                jsonPort.toIntOrNull()?.also { putInt(PREF_KODI_PORT, it) } ?: run { remove(PREF_KODI_PORT) }
                websocketPort.toIntOrNull()
                    ?.also { putInt(PREF_KODI_WEBSOCKET_PORT, it) } ?: run { remove(PREF_KODI_WEBSOCKET_PORT) }
            }
            .apply()

        jsonEngine.initialize()
    }

    /** PRIVATE METHODS ***********************************************************************************************/

    private suspend fun setPlayerElapsedTimeFromProgress(progress: Double) {
        _playerElapsedTime.value = _playerTotalTime.first().times(progress).toLong()
    }

    /** OVERRIDDEN METHODS ********************************************************************************************/

    override fun onKodiNotification(notification: Notification<*>) {
        if (listOf("Player.OnAVStart", "Player.OnResume", "Player.OnStop").contains(notification.method)) {
            launchOnIOThread {
                _playerId.first().also {
                    jsonEngine.fetchPlayerProperties(it)
                    jsonEngine.fetchPlayerItem(it)
                }
            }
        }
        if (notification.data is PlayerOnSeek) {
            notification.data.player.time?.totalMilliseconds?.also { _playerElapsedTime.value = it }
            _isSeeking.value = false
        } else if (notification.data is PlayerOnStop) {
            _playerElapsedTime.value = 0L
        }
        if (notification.method == "Player.OnAVChange") {
            launchOnIOThread { jsonEngine.getGuiProperties() }
        }
    }
}
