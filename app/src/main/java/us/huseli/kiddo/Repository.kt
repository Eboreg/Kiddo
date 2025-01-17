package us.huseli.kiddo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.preference.PreferenceManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Constants.PREF_ASKED_NOTIFICATION_PERMISSION
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_PASSWORD
import us.huseli.kiddo.Constants.PREF_KODI_PORT
import us.huseli.kiddo.Constants.PREF_KODI_USERNAME
import us.huseli.kiddo.Constants.PREF_KODI_WEBSOCKET_PORT
import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.enums.GuiWindow
import us.huseli.kiddo.data.enums.InputAction
import us.huseli.kiddo.data.enums.ListFieldsFiles
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterFieldsEpisodes
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.ListFilterFieldsSongs
import us.huseli.kiddo.data.enums.ListFilterFieldsTvShows
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.requests.ApplicationSetMute
import us.huseli.kiddo.data.requests.ApplicationSetVolume
import us.huseli.kiddo.data.requests.AudioLibraryGetAlbumDetails
import us.huseli.kiddo.data.requests.AudioLibraryGetAlbums
import us.huseli.kiddo.data.requests.AudioLibraryGetSongs
import us.huseli.kiddo.data.requests.FilesGetFileDetails
import us.huseli.kiddo.data.requests.FilesPrepareDownload
import us.huseli.kiddo.data.requests.GuiActivateWindow
import us.huseli.kiddo.data.requests.GuiSetFullscreen
import us.huseli.kiddo.data.requests.InputExecuteAction
import us.huseli.kiddo.data.requests.InputKeyPress
import us.huseli.kiddo.data.requests.InputSendText
import us.huseli.kiddo.data.requests.JsonRpcPermission
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
import us.huseli.kiddo.data.requests.PlaylistGetItems
import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.kiddo.data.requests.PlaylistRemove
import us.huseli.kiddo.data.requests.PlaylistSwap
import us.huseli.kiddo.data.requests.VideoLibraryGetEpisodes
import us.huseli.kiddo.data.requests.VideoLibraryGetMovieDetails
import us.huseli.kiddo.data.requests.VideoLibraryGetMovies
import us.huseli.kiddo.data.requests.VideoLibraryGetSeasons
import us.huseli.kiddo.data.requests.VideoLibraryGetTvShowDetails
import us.huseli.kiddo.data.requests.VideoLibraryGetTvShows
import us.huseli.kiddo.data.requests.VideoLibrarySetMovieDetails
import us.huseli.kiddo.data.requests.VideoLibrarySetTvShowDetails
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.data.types.ListItemFile
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.data.types.VideoDetailsSeason
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsAlbum
import us.huseli.kiddo.data.types.interfaces.IListItemAll
import us.huseli.kiddo.managers.download.DownloadListener
import us.huseli.kiddo.managers.download.DownloadManager
import us.huseli.kiddo.managers.jsonrpc.JsonRpcManager
import us.huseli.kiddo.managers.notification.NotificationManager
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.kiddo.managers.websocket.WebsocketManager
import us.huseli.kiddo.paging.MediaPagingSource
import us.huseli.retaintheme.RetainConnectionError
import us.huseli.retaintheme.RetainHttpError
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.request.Request
import us.huseli.retaintheme.snackbar.SnackbarEngine
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Repository @Inject constructor(
    private val json: JsonRpcManager,
    private val websocket: WebsocketManager,
    @ApplicationContext private val context: Context,
    notification: NotificationManager,
    private val download: DownloadManager,
) : AbstractScopeHolder(), ILogger, SharedPreferences.OnSharedPreferenceChangeListener {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val worker: Worker = Worker(context, json, websocket, download, notification, this)

    private val _askedNotificationPermission =
        MutableStateFlow<Boolean>(preferences.getBoolean(PREF_ASKED_NOTIFICATION_PERMISSION, false))
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
    private val _volume = MutableStateFlow<Int?>(null)

    val askedNotificationPermission = _askedNotificationPermission.asStateFlow()
    @Suppress("DEPRECATION") val connectErrorStrings: Flow<List<String>> = combine(
        websocket.connectError.map { it?.toString() }.distinctUntilChanged(),
        json.connectError.map { it?.toString() }.distinctUntilChanged(),
    ) { websocket, json -> listOfNotNull(websocket, json) }
    val hostname: StateFlow<String?> = json.hostname
    val isMuted = _isMuted.filterNotNull()
    val isPlaying: Flow<Boolean> = _playerSpeed.map { it == 1 }
    val jsonPort: StateFlow<Int> = json.port
    val password: StateFlow<String?> = json.password
    val playerCoverImage: StateFlow<ImageBitmap?> = _playerItem.map {
        val path = it?.art?.poster?.takeIfNotBlank()
            ?: it?.art?.fanart?.takeIfNotBlank()
            ?: it?.fanart?.takeIfNotBlank()

        path?.let { getImageBitmap(path) }
    }.stateWhileSubscribed()
    val playerElapsedTime: StateFlow<Long> = _playerElapsedTime.asStateFlow()
    val playerElapsedTimeSeconds: Flow<Int> = _playerElapsedTime.map { it.div(1000).toInt() }.distinctUntilChanged()
    val playerId: StateFlow<Int?> = _playerId.asStateFlow()
    val playerItem: StateFlow<IListItemAll?> = _playerItem.asStateFlow()
    val playerProgress: StateFlow<Float> = _playerProgress.asStateFlow()
    val playerProperties: StateFlow<PlayerPropertyValue?> = _playerProperties.asStateFlow()
    val playerSpeed: StateFlow<Int?> = _playerSpeed.asStateFlow()
    val playerThumbnailImage: Flow<ImageBitmap?> =
        _playerItem.map { it?.thumbnail?.takeIfNotBlank()?.let { path -> getImageBitmap(path) } }
    val playerTotalTime: StateFlow<Long?> = _playerTotalTime.asStateFlow()
    val playerTotalTimeSeconds: StateFlow<Int?> =
        _playerTotalTime.filterNotNull().map { it.div(1000).toInt() }.distinctUntilChanged().stateWhileSubscribed()
    val username: StateFlow<String?> = json.username
    val volume: StateFlow<Int?> = _volume.asStateFlow()
    val websocketPort: StateFlow<Int> = websocket.port
    val websocketStatus: StateFlow<WebsocketManager.Status> = websocket.status

    init {
        worker.initialize()
        preferences.registerOnSharedPreferenceChangeListener(this)
    }

    fun albumPagingSource(
        filter: ListFilter<ListFilterFieldsAlbums>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
        simpleFilter: AudioLibraryGetAlbums.SimpleFilter? = null,
        includeSingles: Boolean? = null,
        allRoles: Boolean? = null,
    ): MediaPagingSource<AudioDetailsAlbum> {
        return MediaPagingSource { limits ->
            listAlbums(
                filter = filter,
                sort = sort,
                simpleFilter = simpleFilter,
                includeSingles = includeSingles,
                allRoles = allRoles,
                limits = limits,
            )
        }
    }

    suspend fun cycleRepeat() = _playerId.value?.let {
        json.post(PlayerSetRepeat(playerId = it, repeat = PlayerSetRepeat.Repeat.Cycle))
    }

    suspend fun decreasePlayerSpeed() = _playerId.value?.let {
        json.post(PlayerSetSpeed(playerId = it, type = PlayerSetSpeed.ParamType.Decrement))
    }

    @Suppress("unused")
    suspend fun decreasePlayerTempo() = _playerId.value?.let {
        json.post(PlayerSetTempo(playerId = it, type = PlayerSetTempo.ParamType.Decrement))
    }

    suspend fun decreaseVolume() =
        json.post(ApplicationSetVolume(type = ApplicationSetVolume.ParamType.Decrement))

    suspend fun disableSubtitle() = _playerId.value?.let {
        json.post(
            PlayerSetSubtitle(
                playerId = it,
                subtitle = PlayerSetSubtitle.Subtitle.Off,
                enable = false,
            )
        )
    }

    suspend fun downloadFile(path: String, localUri: Uri, extra: Any? = null) {
        val baseUrl = json.url.filterNotNull().first()
        val url = json.post(FilesPrepareDownload(path = path))
            .resultOrNull?.details?.path?.let { "$baseUrl/$it" }

        if (url != null) download.downloadFile(url, localUri, path, extra = extra)
    }

    suspend fun enqueueAlbum(details: IAudioDetailsAlbum) {
        listPlaylists()?.find { it.type == PlaylistType.Audio }?.also { playlist ->
            val request = json.post(
                PlaylistAdd(
                    playlistId = playlist.playlistid,
                    item = PlaylistItem(albumId = details.albumid),
                )
            )

            if (request.resultOrNull == "OK")
                SnackbarEngine.addInfo(context.getString(R.string.x_was_enqueued, details.displayTitle))
        }
    }

    suspend fun enqueueVideo(item: PlaylistItem, title: String) {
        listPlaylists()?.find { it.type == PlaylistType.Video }?.also { playlist ->
            val request = json.post(PlaylistAdd(playlistId = playlist.playlistid, item = item))

            if (request.resultOrNull == "OK") SnackbarEngine.addInfo(context.getString(R.string.x_was_enqueued, title))
        }
    }

    suspend fun executeInputAction(action: InputAction) = json.post(InputExecuteAction(action = action))

    fun flowStreamIntent(
        path: String,
        defaultMimeType: String,
        title: String? = null,
    ): Flow<Intent?> = json.url.filterNotNull().map { baseUrl ->
        getStreamIntent(
            baseUrl = baseUrl,
            path = path,
            defaultMimeType = defaultMimeType,
            title = title,
        )
    }

    suspend fun getFileDetails(file: String): ListItemFile? =
        json.post(FilesGetFileDetails(file = file)).resultOrNull?.filedetails

    suspend fun getAlbumDetails(id: Int): AudioDetailsAlbum? =
        json.post(AudioLibraryGetAlbumDetails(albumId = id)).resultOrNull?.item

    suspend fun getImageBitmap(path: String): ImageBitmap? {
        return try {
            json.hostname.value?.let { host ->
                val url = "http://$host:${json.port.value}/image/${URLEncoder.encode(path, "UTF-8")}"
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

    suspend fun getMovieDetails(id: Int): VideoDetailsMovie? =
        json.post(VideoLibraryGetMovieDetails(movieId = id)).resultOrNull?.item

    suspend fun getStreamIntent(path: String, defaultMimeType: String, title: String? = null): Intent? =
        getStreamIntent(
            baseUrl = json.url.filterNotNull().first(),
            path = path,
            defaultMimeType = defaultMimeType,
            title = title,
        )

    suspend fun getTvShowDetails(tvShowId: Int): VideoDetailsTvShow? =
        json.post(VideoLibraryGetTvShowDetails(tvShowId = tvShowId)).resultOrNull?.item

    suspend fun goToNextItem() = _playerId.value?.let {
        json.post(PlayerGoTo(playerId = it, to = PlayerGoTo.To.Next))
    }

    suspend fun goToPreviousItem() = _playerId.value?.let {
        json.post(PlayerGoTo(playerId = it, to = PlayerGoTo.To.Previous))
    }

    suspend fun increasePlayerSpeed() = _playerId.value?.let {
        json.post(PlayerSetSpeed(playerId = it, type = PlayerSetSpeed.ParamType.Increment))
    }

    @Suppress("unused")
    suspend fun increasePlayerTempo() = _playerId.value?.let {
        json.post(PlayerSetTempo(playerId = it, type = PlayerSetTempo.ParamType.Increment))
    }

    suspend fun increaseVolume() =
        json.post(ApplicationSetVolume(type = ApplicationSetVolume.ParamType.Increment))

    fun incrementPlayerElapsedTime(value: Long) {
        if (!_isSeeking.value) _playerElapsedTime.value += value
    }

    suspend fun listAlbums(
        filter: ListFilter<ListFilterFieldsAlbums>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
        simpleFilter: AudioLibraryGetAlbums.SimpleFilter? = null,
        includeSingles: Boolean? = null,
        allRoles: Boolean? = null,
        limits: ListLimits? = null,
    ): IListResult<AudioDetailsAlbum>? = json.post(
        AudioLibraryGetAlbums(
            filter = filter,
            sort = sort,
            simpleFilter = simpleFilter,
            includeSingles = includeSingles,
            allRoles = allRoles,
            limits = limits,
        )
    ).resultOrNull

    suspend fun listAlbumSongs(albumId: Int): IListResult<AudioDetailsSong>? =
        listSongs(simpleFilter = AudioLibraryGetSongs.SimpleFilter(albumId = albumId))

    suspend fun listEpisodes(
        tvShowId: Int? = null,
        season: Int? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Episode),
        filter: ListFilter<ListFilterFieldsEpisodes>? = null,
        simpleFilter: VideoLibraryGetEpisodes.SimpleFilter? = null,
    ): IListResult<VideoDetailsEpisode>? = json.post(
        VideoLibraryGetEpisodes(
            tvShowId = tvShowId,
            season = season,
            sort = sort,
            filter = filter,
            simpleFilter = simpleFilter,
        )
    ).resultOrNull

    suspend fun listMovies(
        simpleFilter: VideoLibraryGetMovies.SimpleFilter? = null,
        filter: ListFilter<ListFilterFieldsMovies>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
        limits: ListLimits? = null,
    ): IListResult<VideoDetailsMovie>? = json.post(
        VideoLibraryGetMovies(
            simpleFilter = simpleFilter,
            filter = filter,
            sort = sort,
            limits = limits,
        )
    ).resultOrNull

    suspend fun listPlaylistItems(playlistId: Int): IListResult<ListItemAll>? =
        json.post(PlaylistGetItems(playlistId = playlistId)).resultOrNull

    suspend fun listPlaylists() = json.post(PlaylistGetPlaylists()).resultOrNull

    suspend fun listSeasons(
        tvShowId: Int,
        sort: ListSort? = ListSort(method = ListSort.Method.Season),
    ): IListResult<VideoDetailsSeason>? =
        json.post(VideoLibraryGetSeasons(tvShowId = tvShowId, sort = sort)).resultOrNull

    data class SimilarMovie(
        val movie: VideoDetailsMovie,
        val castMatch: Int,
        val tagMatch: Int,
        val genreMatch: Int,
        val crewMatch: Int,
    ) : Comparable<SimilarMovie> {
        val weightedMatch: Int
            get() = crewMatch + castMatch + tagMatch + genreMatch

        override fun compareTo(other: SimilarMovie): Int = weightedMatch - other.weightedMatch
    }

    suspend fun listSimilarMovies(movie: VideoDetailsMovie, max: Int = 10): List<SimilarMovie> {
        val movies = json.post(
            VideoLibraryGetMovies(
                properties = listOf(
                    VideoFieldsMovie.Art,
                    VideoFieldsMovie.Cast,
                    VideoFieldsMovie.Director,
                    VideoFieldsMovie.Genre,
                    VideoFieldsMovie.Tag,
                    VideoFieldsMovie.Title,
                    VideoFieldsMovie.Writer,
                ),
            )
        ).resultOrNull?.items ?: emptyList()

        val similar = movies.filter { it.movieid != movie.movieid }.map { other ->
            val cast = movie.cast?.map { it.name } ?: emptyList()
            val otherCast = other.cast?.map { it.name } ?: emptyList()

            SimilarMovie(
                movie = other,
                castMatch = cast.filter { otherCast.contains(it) }.size,
                tagMatch = movie.tag?.filter { other.tag?.contains(it) == true }?.size ?: 0,
                genreMatch = movie.genre?.filter { other.genre?.contains(it) == true }?.size ?: 0,
                crewMatch = movie.crew.filter { other.crew.contains(it) }.size,
            )
        }

        return similar.sortedDescending().filter { it.weightedMatch > 0 }.take(max)
    }

    suspend fun listSongs(
        filter: ListFilter<ListFilterFieldsSongs>? = null,
        simpleFilter: AudioLibraryGetSongs.SimpleFilter? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Track),
        includeSingles: Boolean? = null,
        singlesOnly: Boolean? = null,
        allRoles: Boolean? = null,
    ): IListResult<AudioDetailsSong>? = json.post(
        AudioLibraryGetSongs(
            sort = sort,
            simpleFilter = simpleFilter,
            filter = filter,
            includeSingles = includeSingles,
            singlesOnly = singlesOnly,
            allRoles = allRoles,
        )
    ).resultOrNull

    suspend fun listTvShows(
        filter: ListFilter<ListFilterFieldsTvShows>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
        simpleFilter: VideoLibraryGetTvShows.SimpleFilter? = null,
        limits: ListLimits? = null,
    ): IListResult<VideoDetailsTvShow>? = json.post(
        VideoLibraryGetTvShows(
            sort = sort,
            filter = filter,
            simpleFilter = simpleFilter,
            limits = limits,
        )
    ).resultOrNull

    fun mergePlayerProperties(value: PlayerPropertyValue) {
        _playerProperties.value = _playerProperties.value?.merge(value) ?: value
    }

    fun moviePagingSource(
        simpleFilter: VideoLibraryGetMovies.SimpleFilter? = null,
        filter: ListFilter<ListFilterFieldsMovies>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
    ): MediaPagingSource<VideoDetailsMovie> = MediaPagingSource { limits ->
        listMovies(simpleFilter = simpleFilter, filter = filter, sort = sort, limits = limits)
    }

    suspend fun openSubtitleSearch() = json.post(GuiActivateWindow(window = GuiWindow.SubtitleSearch))

    suspend fun playAlbum(albumId: Int) = json.post(PlayerOpen.Item(albumId = albumId))

    suspend fun playEpisode(episodeId: Int) = json.post(PlayerOpen.Item(episodeId = episodeId))

    suspend fun playerOpenPlaylist(playlistId: Int, position: Int? = null) =
        json.post(PlayerOpen.Playlist(playlistId = playlistId, position = position))

    suspend fun playMovie(movieId: Int) = json.post(PlayerOpen.Item(movieId = movieId))

    suspend fun playOrPause() = _playerId.value?.let {
        json.post(PlayerPlayPause(playerId = it))
    }

    suspend fun playSong(songId: Int) = json.post(PlayerOpen.Item(songId = songId))

    fun registerDownloadListener(listener: DownloadListener) = download.registerListener(listener)

    fun registerNotificationListener(listener: KodiNotificationListener) = websocket.registerListener(listener)

    @Suppress("unused")
    fun registerNotificationListener(listener: (Notification<*>) -> Unit) =
        websocket.registerListener(KodiNotificationListener { listener(it) })

    suspend fun removePlaylistItem(playlistId: Int, position: Int) =
        json.post(PlaylistRemove(playlistId = playlistId, position = position))

    fun seekFinish(progress: Float) {
        setPlayerElapsedTimeFromProgress(progress.toDouble())
        _playerId.value?.also {
            launchOnIOThread {
                json.post(PlayerSeek(playerId = it, value = PlayerSeek.Value(percentage = progress * 100f)))
            }
        }
    }

    fun seekInProgress(progress: Float) {
        _isSeeking.value = true
        _playerProgress.value = progress
        setPlayerElapsedTimeFromProgress(progress.toDouble())
    }

    suspend fun sendKeypress(key: String) = json.post(InputKeyPress(key))

    suspend fun sendText(text: String, done: Boolean? = null) = json.post(InputSendText(text = text, done = done))

    fun setAskedNotificationPermission() {
        preferences.edit().putBoolean(PREF_ASKED_NOTIFICATION_PERMISSION, true).apply()
    }

    suspend fun setAudioStream(index: Int) = _playerId.value?.let {
        json.post(PlayerSetAudioStream(playerId = it, streamIndex = index))
    }

    fun setForeground(value: Boolean) = websocket.setForeground(value)

    suspend fun setFullscreen() = json.post(GuiSetFullscreen(fullscreen = GlobalToggle.True))

    suspend fun setMovieWatched(movieId: Int, value: Boolean) = json.post(
        VideoLibrarySetMovieDetails(
            VideoLibrarySetMovieDetails.Params(movieid = movieId, playcount = if (value) 1 else 0),
        )
    )

    suspend fun setMute(value: Boolean) = json.post(ApplicationSetMute(mute = GlobalToggle.fromValue(value)))

    suspend fun setSubtitle(index: Int) = _playerId.value?.let {
        json.post(PlayerSetSubtitle(playerId = it, subtitleIndex = index, enable = true))
    }

    suspend fun setTvShowWatched(tvShowId: Int, value: Boolean) = json.post(
        VideoLibrarySetTvShowDetails(
            VideoLibrarySetTvShowDetails.Params(tvshowid = tvShowId, playcount = if (value) 1 else 0),
        )
    )

    suspend fun setVolume(value: Int, rateLimited: Boolean = false) {
        val request = ApplicationSetVolume(volume = value)

        if (rateLimited) json.postRateLimited(request, 500L)
        else json.post(request)
    }

    suspend fun stop() = _playerId.value?.let { json.post(PlayerStop(playerId = it)) }

    suspend fun swapPlaylistPositions(playlistId: Int, from: Int, to: Int) =
        json.post(PlaylistSwap(playlistId = playlistId, position1 = from, position2 = to))

    suspend fun toggleShuffle() = _playerId.value?.let {
        json.post(PlayerSetShuffle(playerId = it, shuffle = GlobalToggle.Toggle))
    }

    fun tvShowPagingSource(
        filter: ListFilter<ListFilterFieldsTvShows>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
        simpleFilter: VideoLibraryGetTvShows.SimpleFilter? = null,
    ): MediaPagingSource<VideoDetailsTvShow> = MediaPagingSource { limits ->
        listTvShows(filter = filter, sort = sort, simpleFilter = simpleFilter, limits = limits)
    }

    fun unregisterDownloadListener(listener: DownloadListener) = download.unregisterListener(listener)

    fun unregisterNotificationListener(listener: KodiNotificationListener) =
        websocket.unregisterListener(listener)

    fun updateIsMuted(value: Boolean) {
        _isMuted.value = value
    }

    fun updatePermissions(value: JsonRpcPermission.Result) {
        _permissions.value = value
    }

    fun updatePlayerElapsedTime(value: Long, resetIsSeeking: Boolean = false) {
        if (!_isSeeking.value || resetIsSeeking) {
            _playerElapsedTime.value = value
            if (resetIsSeeking) _isSeeking.value = false
        }
    }

    fun updatePlayerId(value: Int?) {
        _playerId.value = value
    }

    fun updatePlayerItem(value: IListItemAll?) {
        _playerItem.value = value
    }

    fun updatePlayerProgress(value: Float) {
        if (!_isSeeking.value) _playerProgress.value = value
    }

    fun updatePlayerProperties(value: PlayerPropertyValue) {
        _playerProperties.value = value
    }

    fun updatePlayerSpeed(value: Int) {
        _playerSpeed.value = value
    }

    fun updatePlayerTotalTime(value: Long) {
        _playerTotalTime.value = value
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

    fun updateVolume(value: Int) {
        _volume.value = value
    }

    /** PRIVATE METHODS ***********************************************************************************************/

    private suspend fun getStreamIntent(
        baseUrl: String,
        path: String,
        defaultMimeType: String,
        title: String? = null,
    ): Intent? {
        val mimeBase = defaultMimeType.takeWhile { it != '/' } + "/"
        val details = json.post(
            FilesGetFileDetails(file = path, properties = listOf(ListFieldsFiles.MimeType, ListFieldsFiles.Title))
        ).resultOrNull?.filedetails
        val mimeType = details?.mimetype?.takeIf { it.startsWith(mimeBase) } ?: defaultMimeType
        val url = json.post(FilesPrepareDownload(path = path))
            .resultOrNull?.details?.path?.let { "$baseUrl/$it" }

        return url?.let {
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndTypeAndNormalize(Uri.parse(it), mimeType)
                (title ?: details?.title)?.also { putExtra("title", it) }
            }
        }
    }

    private fun setPlayerElapsedTimeFromProgress(progress: Double) {
        _playerTotalTime.value?.also {
            _playerElapsedTime.value = it.times(progress).toLong()
        }
    }

    /** OVERRIDDEN METHODS ********************************************************************************************/

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when (key) {
            PREF_ASKED_NOTIFICATION_PERMISSION -> {
                _askedNotificationPermission.value = preferences.getBoolean(key, false)
            }
        }
    }
}
