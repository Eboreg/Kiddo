package us.huseli.kiddo

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_PASSWORD
import us.huseli.kiddo.Constants.PREF_KODI_PORT
import us.huseli.kiddo.Constants.PREF_KODI_USERNAME
import us.huseli.kiddo.Constants.PREF_KODI_WEBSOCKET_PORT
import us.huseli.kiddo.data.KodiJsonRpcResponse
import us.huseli.kiddo.data.enums.ApplicationPropertyName
import us.huseli.kiddo.data.enums.AudioFieldsAlbum
import us.huseli.kiddo.data.enums.AudioFieldsSong
import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.enums.GuiPropertyName
import us.huseli.kiddo.data.enums.GuiWindow
import us.huseli.kiddo.data.enums.InputAction
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.enums.PlayerPropertyName
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.interfaces.IHasPlayerId
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.interfaces.IHasPlayerTime
import us.huseli.kiddo.data.interfaces.IHasPlayerTotalTime
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.ApplicationOnVolumeChanged
import us.huseli.kiddo.data.notifications.data.PlayerOnPropertyChanged
import us.huseli.kiddo.data.notifications.data.PlayerOnStop
import us.huseli.kiddo.data.requests.ApplicationGetProperties
import us.huseli.kiddo.data.requests.ApplicationSetMute
import us.huseli.kiddo.data.requests.ApplicationSetVolume
import us.huseli.kiddo.data.requests.AudioLibraryGetAlbumDetails
import us.huseli.kiddo.data.requests.AudioLibraryGetAlbums
import us.huseli.kiddo.data.requests.AudioLibraryGetSongs
import us.huseli.kiddo.data.requests.GuiActivateWindow
import us.huseli.kiddo.data.requests.GuiGetProperties
import us.huseli.kiddo.data.requests.GuiSetFullscreen
import us.huseli.kiddo.data.requests.InputExecuteAction
import us.huseli.kiddo.data.requests.InputKeyPress
import us.huseli.kiddo.data.requests.InputSendText
import us.huseli.kiddo.data.requests.JsonRpcPermission
import us.huseli.kiddo.data.requests.PlayerGetActivePlayers
import us.huseli.kiddo.data.requests.PlayerGetItem
import us.huseli.kiddo.data.requests.PlayerGetProperties
import us.huseli.kiddo.data.requests.PlayerGoTo
import us.huseli.kiddo.data.requests.PlayerOpen
import us.huseli.kiddo.data.requests.PlayerPlayPause
import us.huseli.kiddo.data.requests.PlayerSeek
import us.huseli.kiddo.data.requests.PlayerSetAudioStream
import us.huseli.kiddo.data.requests.PlayerSetRepeat
import us.huseli.kiddo.data.requests.PlayerSetShuffle
import us.huseli.kiddo.data.requests.PlayerSetSpeed
import us.huseli.kiddo.data.requests.PlayerSetSubtitle
import us.huseli.kiddo.data.requests.PlayerSetTempo
import us.huseli.kiddo.data.requests.PlayerStop
import us.huseli.kiddo.data.requests.PlaylistAdd
import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.kiddo.data.requests.PlaylistRemove
import us.huseli.kiddo.data.requests.VideoLibraryGetMovieDetails
import us.huseli.kiddo.data.requests.VideoLibraryGetMovies
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.data.types.ListFilterAlbums
import us.huseli.kiddo.data.types.ListFilterMovies
import us.huseli.kiddo.data.types.ListFilterSongs
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsAlbum
import us.huseli.kiddo.data.types.interfaces.IListItemAll
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsMovie
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
) : KodiNotificationListener, KodiResponseListener, AbstractScopeHolder(), ILogger {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val _isMuted = MutableStateFlow<Boolean?>(null)
    private val _isSeeking = MutableStateFlow(false)
    private val _permissions = MutableStateFlow<JsonRpcPermission.Result?>(null)
    private val _playerElapsedTime = MutableStateFlow(0L)
    private val _playerId = MutableStateFlow<Int?>(null)
    private val _playerItem = MutableStateFlow<IListItemAll?>(null)
    private val _playerProgress = MutableStateFlow(0f)
    private val _playerProperties = MutableStateFlow<PlayerPropertyValue?>(null)
    private val _playerSpeed = MutableStateFlow<Int?>(null)
    private val _playerTotalTime = MutableStateFlow<Long?>(null)
    private val _timestamp = MutableStateFlow<Long>(System.currentTimeMillis())
    private val _volume = MutableStateFlow<Int?>(null)

    val connectErrorStrings = combine(
        websocketEngine.connectError.map { it?.message ?: it?.toString() }.distinctUntilChanged(),
        jsonEngine.connectError.map { it?.message ?: it?.toString() }.distinctUntilChanged(),
    ) { websocket, json ->
        listOfNotNull(websocket, json)
    }.stateWhileSubscribed(emptyList())
    val hostname = jsonEngine.hostname
    val isMuted = _isMuted.filterNotNull().stateWhileSubscribed(false)
    val isPlaying = _playerSpeed.map { it == 1 }.stateWhileSubscribed(false)
    val jsonPort = jsonEngine.port
    val password = jsonEngine.password
    val playerCoverImage = _playerItem.map {
        val path = it?.art?.poster?.takeIfNotBlank()
            ?: it?.art?.fanart?.takeIfNotBlank()
            ?: it?.fanart?.takeIfNotBlank()

        path?.let { getImageBitmap(path) }
    }.stateWhileSubscribed()
    val playerElapsedTimeSeconds =
        _playerElapsedTime.map { it.div(1000).toInt() }.distinctUntilChanged().stateWhileSubscribed()
    val playerItem = _playerItem.asStateFlow()
    val playerProgress = _playerProgress.asStateFlow()
    val playerProperties = _playerProperties.stateWhileSubscribed()
    val playerThumbnailImage: StateFlow<ImageBitmap?> = _playerItem.map {
        it?.thumbnail?.takeIfNotBlank()?.let { path -> getImageBitmap(path) }
    }.stateWhileSubscribed()
    val playerTotalTimeSeconds =
        _playerTotalTime.filterNotNull().map { it.div(1000).toInt() }.distinctUntilChanged().stateWhileSubscribed()
    val playlists: StateFlow<List<PlaylistGetPlaylists.ResultItem>> = jsonEngine.playlists
    val username = jsonEngine.username
    val volume = _volume.asStateFlow()
    val websocketPort = websocketEngine.port

    init {
        websocketEngine.addListener(this)
        jsonEngine.addListener(this)

        reset()

        launchOnIOThread {
            jsonEngine.post(JsonRpcPermission())?.also { _permissions.value = it }
        }

        // Listen for error messages on JSON and websockets engines separately
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

        // Update _playerElapsedTime when it is changed by updated player properties, and user is not currently seeking
        launchOnMainThread {
            combine(
                _playerProperties.map { it?.time?.totalMilliseconds }.filterNotNull().distinctUntilChanged(),
                _isSeeking,
            ) { elapsed, isSeeking ->
                elapsed.takeIf { !isSeeking }
            }.filterNotNull().collect { elapsed ->
                _playerElapsedTime.value = elapsed
            }
        }

        // When _playerId is changed for whatever reason, refetch player properties & current item
        launchOnIOThread {
            _playerId.filterNotNull().distinctUntilChanged().collect { playerId ->
                fetchPlayerProperties(playerId)
                fetchPlayerItem(playerId)
            }
        }

        // Update _playerElapsedTime in ~500ms intervals while player is playing or seeking (but not while user is
        // currently seeking)
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

        // Update _playerProgress from _playerElapsedTime and _playerTotalTime if the latter is known, and user is
        // not currently seeking
        launchOnMainThread {
            combine(
                _playerElapsedTime,
                _playerTotalTime.filterNotNull(),
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
    }

    fun addNotificationListener(listener: KodiNotificationListener) = websocketEngine.addListener(listener)

    suspend fun cycleRepeat() = _playerId.value?.let {
        jsonEngine.post(PlayerSetRepeat(playerId = it, repeat = PlayerSetRepeat.Repeat.Cycle))
    }

    suspend fun decreasePlayerSpeed() = _playerId.value?.let {
        jsonEngine.post(PlayerSetSpeed(playerId = it, type = PlayerSetSpeed.ParamType.Decrement))
    }

    @Suppress("unused")
    suspend fun decreasePlayerTempo() = _playerId.value?.let {
        jsonEngine.post(PlayerSetTempo(playerId = it, type = PlayerSetTempo.ParamType.Decrement))
    }

    suspend fun decreaseVolume() =
        jsonEngine.post(ApplicationSetVolume(type = ApplicationSetVolume.ParamType.Decrement))

    suspend fun disableSubtitle() = _playerId.value?.let {
        jsonEngine.post(
            PlayerSetSubtitle(
                playerId = it,
                subtitle = PlayerSetSubtitle.Subtitle.Off,
                enable = false,
            )
        )
    }

    suspend fun enqueueAlbum(details: IAudioDetailsAlbum) {
        getPlaylists()?.find { it.type == PlaylistType.Audio }?.also { playlist ->
            val result = jsonEngine.post(
                PlaylistAdd(
                    playlistId = playlist.playlistid,
                    item = PlaylistItem(albumId = details.albumid),
                )
            )

            if (result == "OK") SnackbarEngine.addInfo(context.getString(R.string.x_was_enqueued, details.displayTitle))
        }
    }

    suspend fun enqueueMovie(details: IVideoDetailsMovie) {
        getPlaylists()?.find { it.type == PlaylistType.Video }?.also { playlist ->
            val result = jsonEngine.post(
                PlaylistAdd(
                    playlistId = playlist.playlistid,
                    item = PlaylistItem(movieId = details.movieid),
                )
            )

            if (result == "OK") SnackbarEngine.addInfo(context.getString(R.string.x_was_enqueued, details.displayTitle))
        }
    }

    suspend fun executeInputAction(action: InputAction) = jsonEngine.post(InputExecuteAction(action = action))

    suspend fun fetchPlaylists() = jsonEngine.fetchPlaylists()

    fun flowPlaylistItems(playlistId: Int): Flow<List<IListItemAll>> =
        jsonEngine.playlistItems.map { it[playlistId] ?: emptyList() }

    suspend fun getAlbumDetails(id: Int): AudioDetailsAlbum? {
        return jsonEngine.post(
            AudioLibraryGetAlbumDetails(
                albumId = id,
                properties = listOf(
                    AudioFieldsAlbum.AlbumDuration,
                    AudioFieldsAlbum.AlbumStatus,
                    AudioFieldsAlbum.Art,
                    AudioFieldsAlbum.Artist,
                    AudioFieldsAlbum.ArtistId,
                    AudioFieldsAlbum.Compilation,
                    AudioFieldsAlbum.Description,
                    AudioFieldsAlbum.DisplayArtist,
                    AudioFieldsAlbum.FanArt,
                    AudioFieldsAlbum.Genre,
                    AudioFieldsAlbum.Mood,
                    AudioFieldsAlbum.PlayCount,
                    AudioFieldsAlbum.Rating,
                    AudioFieldsAlbum.SongGenres,
                    AudioFieldsAlbum.Style,
                    AudioFieldsAlbum.Theme,
                    AudioFieldsAlbum.Thumbnail,
                    AudioFieldsAlbum.Title,
                    AudioFieldsAlbum.Type,
                    AudioFieldsAlbum.Votes,
                    AudioFieldsAlbum.Year,
                )
            )
        )?.albumdetails
    }

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
                    VideoFieldsMovie.LastPlayed,
                    VideoFieldsMovie.OriginalTitle,
                    VideoFieldsMovie.PlayCount,
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
        )?.moviedetails
    }

    suspend fun getPlaylists() = jsonEngine.post(PlaylistGetPlaylists())

    suspend fun goToNextItem() = _playerId.value?.let {
        jsonEngine.post(PlayerGoTo(playerId = it, to = PlayerGoTo.To.Next))
    }

    suspend fun goToPreviousItem() = _playerId.value?.let {
        jsonEngine.post(PlayerGoTo(playerId = it, to = PlayerGoTo.To.Previous))
    }

    suspend fun increasePlayerSpeed() = _playerId.value?.let {
        jsonEngine.post(PlayerSetSpeed(playerId = it, type = PlayerSetSpeed.ParamType.Increment))
    }

    @Suppress("unused")
    suspend fun increasePlayerTempo() = _playerId.value?.let {
        jsonEngine.post(PlayerSetTempo(playerId = it, type = PlayerSetTempo.ParamType.Increment))
    }

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

    suspend fun listAlbumSongs(albumId: Int): List<AudioDetailsSong>? =
        listSongs(simpleFilter = AudioLibraryGetSongs.SimpleFilter(albumId = albumId))

    suspend fun listMovies(
        simpleFilter: VideoLibraryGetMovies.SimpleFilter? = null,
        filter: ListFilterMovies? = null,
        sort: ListSort? = null,
    ): List<VideoDetailsMovie>? =
        jsonEngine.post(
            VideoLibraryGetMovies(
                properties = listOf(
                    VideoFieldsMovie.Art,
                    VideoFieldsMovie.Director,
                    VideoFieldsMovie.File,
                    VideoFieldsMovie.Genre,
                    VideoFieldsMovie.LastPlayed,
                    VideoFieldsMovie.PlayCount,
                    VideoFieldsMovie.Rating,
                    VideoFieldsMovie.Runtime,
                    VideoFieldsMovie.Tagline,
                    VideoFieldsMovie.Title,
                    VideoFieldsMovie.Year,
                ),
                simpleFilter = simpleFilter,
                filter = filter,
                sort = sort ?: ListSort(method = ListSort.Method.Title),
            )
        )?.movies

    suspend fun listSongs(
        filter: ListFilterSongs? = null,
        simpleFilter: AudioLibraryGetSongs.SimpleFilter? = null,
    ): List<AudioDetailsSong>? = jsonEngine.post(
        AudioLibraryGetSongs(
            properties = listOf(
                AudioFieldsSong.Artist,
                AudioFieldsSong.Bitrate,
                AudioFieldsSong.Disc,
                AudioFieldsSong.DisplayArtist,
                AudioFieldsSong.Duration,
                AudioFieldsSong.File,
                AudioFieldsSong.Genre,
                AudioFieldsSong.Mood,
                AudioFieldsSong.PlayCount,
                AudioFieldsSong.Title,
                AudioFieldsSong.Track,
                AudioFieldsSong.Year,
            ),
            sort = ListSort(method = ListSort.Method.Track),
            simpleFilter = simpleFilter,
            filter = filter,
        )
    )?.songs

    suspend fun openSubtitleSearch() = jsonEngine.post(GuiActivateWindow(window = GuiWindow.SubtitleSearch))

    suspend fun playerOpenPlaylist(playlistId: Int, position: Int? = null) =
        jsonEngine.post(PlayerOpen.Playlist(playlistId = playlistId, position = position))

    suspend fun playAlbum(albumId: Int) = jsonEngine.post(PlayerOpen.Item(albumId = albumId))

    suspend fun playMovie(movieId: Int) = jsonEngine.post(PlayerOpen.Item(movieId = movieId))

    suspend fun playOrPause() = _playerId.value?.let {
        jsonEngine.post(PlayerPlayPause(playerId = it))
    }

    suspend fun playSong(songId: Int) = jsonEngine.post(PlayerOpen.Item(songId = songId))

    fun removeNotificationListener(listener: KodiNotificationListener) = websocketEngine.removeListener(listener)

    suspend fun removePlaylistItem(playlistId: Int, position: Int) =
        jsonEngine.post(PlaylistRemove(playlistId = playlistId, position = position))

    fun seekFinish(progress: Float) {
        setPlayerElapsedTimeFromProgress(progress.toDouble())
        _playerId.value?.also {
            launchOnIOThread {
                jsonEngine.post(PlayerSeek(playerId = it, value = PlayerSeek.Value(percentage = progress * 100f)))
                _isSeeking.value = false
            }
        }
    }

    fun seekInProgress(progress: Float) {
        _isSeeking.value = true
        _playerProgress.value = progress
        setPlayerElapsedTimeFromProgress(progress.toDouble())
    }

    suspend fun sendKeypress(key: String) = jsonEngine.post(InputKeyPress(key))

    suspend fun sendText(text: String, done: Boolean? = null) = jsonEngine.post(InputSendText(text = text, done = done))

    suspend fun setAudioStream(index: Int) = _playerId.value?.let {
        jsonEngine.post(PlayerSetAudioStream(playerId = it, streamIndex = index))
    }

    suspend fun setFullscreen() = jsonEngine.post(GuiSetFullscreen(fullscreen = GlobalToggle.True))

    suspend fun setMute(value: Boolean) = jsonEngine.post(ApplicationSetMute(mute = GlobalToggle.fromValue(value)))

    suspend fun setSubtitle(index: Int) = _playerId.value?.let {
        jsonEngine.post(PlayerSetSubtitle(playerId = it, subtitleIndex = index, enable = true))
    }

    suspend fun setVolume(value: Int, rateLimited: Boolean = false) {
        val request = ApplicationSetVolume(volume = value)

        if (rateLimited) jsonEngine.postRateLimited(request, 500L)
        else jsonEngine.post(request)
    }

    suspend fun stop() = _playerId.value?.let { jsonEngine.post(PlayerStop(playerId = it)) }

    suspend fun swapPlaylistPositions(playlistId: Int, from: Int, to: Int) =
        jsonEngine.swapPlaylistPositions(playlistId, from, to)

    suspend fun toggleShuffle() = _playerId.value?.let {
        jsonEngine.post(PlayerSetShuffle(playerId = it, shuffle = GlobalToggle.Toggle))
    }

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
    }

    /** PRIVATE METHODS ***********************************************************************************************/

    private suspend fun fetchPlayerItem(playerId: Int) {
        jsonEngine.post(
            PlayerGetItem(
                playerId = playerId,
                properties = listOf(
                    ListFieldsAll.Album,
                    ListFieldsAll.AlbumId,
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

    private suspend fun fetchPlayerProperties(playerId: Int) {
        jsonEngine.post(
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

    private suspend fun fetchGuiProperties() =
        jsonEngine.post(GuiGetProperties(properties = listOf(GuiPropertyName.Fullscreen)))

    private fun reset() {
        launchOnIOThread {
            jsonEngine.post(
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
        launchOnIOThread { jsonEngine.post(PlayerGetActivePlayers()) }
        launchOnIOThread { fetchGuiProperties() }
    }

    private fun setPlayerElapsedTimeFromProgress(progress: Double) {
        _playerTotalTime.value?.also {
            _playerElapsedTime.value = it.times(progress).toLong()
        }
    }

    /** OVERRIDDEN METHODS ********************************************************************************************/

    override fun onKodiNotification(notification: Notification<*>) {
        if (listOf("Player.OnAVStart", "Player.OnResume", "Player.OnStop").contains(notification.method)) {
            _playerId.value?.also {
                launchOnIOThread {
                    fetchPlayerProperties(it)
                    fetchPlayerItem(it)
                }
            }
        }
        if (notification.data is PlayerOnStop) {
            _playerItem.value = null
        }
        if (notification.method == "Player.OnAVChange") {
            launchOnIOThread { fetchGuiProperties() }
        }
        if (notification.data is ApplicationOnVolumeChanged) {
            _volume.value = notification.data.volume
            _isMuted.value = notification.data.muted
        }
        if (notification.data is PlayerOnPropertyChanged) {
            _playerProperties.value =
                _playerProperties.value?.merge(notification.data.property) ?: notification.data.property
        }
        if (notification.data is IHasPlayerSpeed) {
            notification.data.speed?.also { _playerSpeed.value = it }
        }
        if (notification.data is IHasPlayerId) {
            _playerId.value = notification.data.playerid
        }
        if (notification.data is IHasPlayerTime) {
            notification.data.time?.totalMilliseconds?.also { _playerElapsedTime.value = it }
            _isSeeking.value = false
        }
    }

    override fun onKodiResponse(response: KodiJsonRpcResponse<*>) {
        if (response.request is ApplicationSetVolume && response.result is Int) {
            _volume.value = response.result
        }
        if (response.result is ApplicationGetProperties.Result) {
            _volume.value = response.result.volume
            _isMuted.value = response.result.muted
        }
        if (response.result is IHasPlayerSpeed) {
            response.result.speed?.also { _playerSpeed.value = it }
        }
        if (response.result is PlayerPropertyValue) {
            _playerProperties.value = response.result
        }
        if (response.result is IHasPlayerTotalTime) {
            response.result.totaltime?.totalMilliseconds?.takeIf { it > 0 }?.also { _playerTotalTime.value = it }
        }
        if (response.result is PlayerGetItem.Result) {
            _playerItem.value = response.result.item
        }
        if (response.result is IHasPlayerId) {
            _playerId.value = response.result.playerid
        }
        if (response.method == "Player.GetActivePlayers") {
            @Suppress("UNCHECKED_CAST")
            (response.result as? Iterable<PlayerGetActivePlayers.ResultItem>)?.firstOrNull()?.also {
                _playerId.value = it.playerid
            }
        }
        if (response.result is IHasPlayerTime) {
            response.result.time?.totalMilliseconds?.also { _playerElapsedTime.value = it }
            _isSeeking.value = false
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
}
