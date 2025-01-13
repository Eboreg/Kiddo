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
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Constants.PREF_KODI_HOST
import us.huseli.kiddo.Constants.PREF_KODI_PASSWORD
import us.huseli.kiddo.Constants.PREF_KODI_PORT
import us.huseli.kiddo.Constants.PREF_KODI_USERNAME
import us.huseli.kiddo.Constants.PREF_KODI_WEBSOCKET_PORT
import us.huseli.kiddo.data.enums.AudioFieldsAlbum
import us.huseli.kiddo.data.enums.AudioFieldsSong
import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.enums.GuiWindow
import us.huseli.kiddo.data.enums.InputAction
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.enums.ListFieldsFiles
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterFieldsEpisodes
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.ListFilterFieldsSongs
import us.huseli.kiddo.data.enums.ListFilterFieldsTvShows
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.enums.VideoFieldsEpisode
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.enums.VideoFieldsSeason
import us.huseli.kiddo.data.enums.VideoFieldsTvShow
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
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.data.types.VideoDetailsSeason
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsAlbum
import us.huseli.kiddo.data.types.interfaces.IListItemAll
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
    private val jsonEngine: KodiJsonRpcEngine,
    private val websocketEngine: KodiWebsocketEngine,
    @ApplicationContext private val context: Context,
) : AbstractScopeHolder(), ILogger {
    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val worker: Worker = Worker(jsonEngine, websocketEngine, this)

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

    @Suppress("DEPRECATION")
    val connectErrorStrings: Flow<List<String>> = combine(
        websocketEngine.connectError.map { it?.toString() }.distinctUntilChanged(),
        jsonEngine.connectError.map { it?.toString() }.distinctUntilChanged(),
    ) { websocket, json -> listOfNotNull(websocket, json) }
    val hostname: StateFlow<String?> = jsonEngine.hostname
    val isMuted = _isMuted.filterNotNull()
    val isPlaying: Flow<Boolean> = _playerSpeed.map { it == 1 }
    val jsonPort: StateFlow<Int> = jsonEngine.port
    val password: StateFlow<String?> = jsonEngine.password
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
    val username: StateFlow<String?> = jsonEngine.username
    val volume: StateFlow<Int?> = _volume.asStateFlow()
    val websocketPort: StateFlow<Int> = websocketEngine.port
    val websocketStatus: StateFlow<KodiWebsocketEngine.Status> = websocketEngine.status

    init {
        worker.initialize()
    }

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
        listPlaylists()?.find { it.type == PlaylistType.Audio }?.also { playlist ->
            val request = jsonEngine.post(
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
            val request = jsonEngine.post(PlaylistAdd(playlistId = playlist.playlistid, item = item))

            if (request.resultOrNull == "OK") SnackbarEngine.addInfo(context.getString(R.string.x_was_enqueued, title))
        }
    }

    suspend fun executeInputAction(action: InputAction) = jsonEngine.post(InputExecuteAction(action = action))

    fun flowStreamIntent(path: String, defaultMimeType: String): Flow<Intent?> = jsonEngine.url.map { baseUrl ->
        val mimeBase = defaultMimeType.takeWhile { it != '/' } + "/"
        val details = jsonEngine.post(
            FilesGetFileDetails(file = path, properties = listOf(ListFieldsFiles.MimeType, ListFieldsFiles.Title))
        ).resultOrNull?.filedetails
        val mimeType = details?.mimetype?.takeIf { it.startsWith(mimeBase) } ?: defaultMimeType
        val url = jsonEngine.post(FilesPrepareDownload(path = path))
            .resultOrNull?.details?.path?.let { "$baseUrl/$it" }

        url?.let {
            Intent(Intent.ACTION_VIEW).apply {
                setDataAndTypeAndNormalize(Uri.parse(it), mimeType)
                details?.title?.also { putExtra("title", it) }
            }
        }
    }

    suspend fun getAlbumDetails(id: Int): AudioDetailsAlbum? = jsonEngine.post(
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
    ).resultOrNull?.albumdetails

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

    suspend fun getMovieDetails(id: Int): VideoDetailsMovie? = jsonEngine.post(
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
                VideoFieldsMovie.Resume,
                VideoFieldsMovie.Runtime,
                VideoFieldsMovie.Tag,
                VideoFieldsMovie.Tagline,
                VideoFieldsMovie.Thumbnail,
                VideoFieldsMovie.Title,
                VideoFieldsMovie.Votes,
                VideoFieldsMovie.Year,
            ),
        )
    ).resultOrNull?.moviedetails

    suspend fun getTvShowDetails(tvShowId: Int): VideoDetailsTvShow? = jsonEngine.post(
        VideoLibraryGetTvShowDetails(
            tvShowId = tvShowId,
            properties = listOf(
                VideoFieldsTvShow.Art,
                VideoFieldsTvShow.Cast,
                VideoFieldsTvShow.Episode,
                VideoFieldsTvShow.Fanart,
                VideoFieldsTvShow.Genre,
                VideoFieldsTvShow.OriginalTitle,
                VideoFieldsTvShow.PlayCount,
                VideoFieldsTvShow.Plot,
                VideoFieldsTvShow.Rating,
                VideoFieldsTvShow.Runtime,
                VideoFieldsTvShow.Season,
                VideoFieldsTvShow.Tag,
                VideoFieldsTvShow.Thumbnail,
                VideoFieldsTvShow.Title,
                VideoFieldsTvShow.Votes,
                VideoFieldsTvShow.WatchedEpisodes,
                VideoFieldsTvShow.Year,
            ),
        )
    ).resultOrNull?.tvshowdetails

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

    fun incrementPlayerElapsedTime(value: Long) {
        if (!_isSeeking.value) _playerElapsedTime.value += value
    }

    suspend fun listAlbums(
        filter: ListFilter<ListFilterFieldsAlbums>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
        simpleFilter: AudioLibraryGetAlbums.SimpleFilter? = null,
        includeSingles: Boolean? = null,
        allRoles: Boolean? = null,
    ): List<AudioDetailsAlbum>? = jsonEngine.post(
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
            sort = sort,
            simpleFilter = simpleFilter,
            includeSingles = includeSingles,
            allRoles = allRoles,
        )
    ).resultOrNull?.albums

    suspend fun listAlbumSongs(albumId: Int): List<AudioDetailsSong>? =
        listSongs(simpleFilter = AudioLibraryGetSongs.SimpleFilter(albumId = albumId))

    suspend fun listEpisodes(
        tvShowId: Int? = null,
        season: Int? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Episode),
        filter: ListFilter<ListFilterFieldsEpisodes>? = null,
        simpleFilter: VideoLibraryGetEpisodes.SimpleFilter? = null,
    ): List<VideoDetailsEpisode>? = jsonEngine.post(
        VideoLibraryGetEpisodes(
            tvShowId = tvShowId,
            season = season,
            properties = listOf(
                VideoFieldsEpisode.Art,
                VideoFieldsEpisode.Episode,
                VideoFieldsEpisode.FanArt,
                VideoFieldsEpisode.File,
                VideoFieldsEpisode.OriginalTitle,
                VideoFieldsEpisode.PlayCount,
                VideoFieldsEpisode.Plot,
                VideoFieldsEpisode.Rating,
                VideoFieldsEpisode.Resume,
                VideoFieldsEpisode.Runtime,
                VideoFieldsEpisode.Season,
                VideoFieldsEpisode.SeasonId,
                VideoFieldsEpisode.Thumbnail,
                VideoFieldsEpisode.Title,
            ),
            sort = sort,
            filter = filter,
            simpleFilter = simpleFilter,
        )
    ).resultOrNull?.episodes

    suspend fun listMovies(
        simpleFilter: VideoLibraryGetMovies.SimpleFilter? = null,
        filter: ListFilter<ListFilterFieldsMovies>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
    ): List<VideoDetailsMovie>? = jsonEngine.post(
        VideoLibraryGetMovies(
            properties = listOf(
                VideoFieldsMovie.Art,
                VideoFieldsMovie.Director,
                VideoFieldsMovie.File,
                VideoFieldsMovie.Genre,
                VideoFieldsMovie.LastPlayed,
                VideoFieldsMovie.PlayCount,
                VideoFieldsMovie.Rating,
                VideoFieldsMovie.Resume,
                VideoFieldsMovie.Runtime,
                VideoFieldsMovie.Tagline,
                VideoFieldsMovie.Title,
                VideoFieldsMovie.Year,
            ),
            simpleFilter = simpleFilter,
            filter = filter,
            sort = sort,
        )
    ).resultOrNull?.movies

    suspend fun listPlaylistItems(playlistId: Int): List<ListItemAll> = jsonEngine.post(
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
    ).resultOrNull?.items ?: emptyList()

    suspend fun listPlaylists() = jsonEngine.post(PlaylistGetPlaylists()).resultOrNull

    suspend fun listSeasons(
        tvShowId: Int,
        sort: ListSort? = ListSort(method = ListSort.Method.Season),
    ): List<VideoDetailsSeason>? = jsonEngine.post(
        VideoLibraryGetSeasons(
            tvShowId = tvShowId,
            properties = listOf(
                VideoFieldsSeason.Art,
                VideoFieldsSeason.Episode,
                VideoFieldsSeason.PlayCount,
                VideoFieldsSeason.Season,
                VideoFieldsSeason.Thumbnail,
                VideoFieldsSeason.Title,
                VideoFieldsSeason.WatchedEpisodes,
            ),
            sort = sort,
        )
    ).resultOrNull?.seasons

    suspend fun listSongs(
        filter: ListFilter<ListFilterFieldsSongs>? = null,
        simpleFilter: AudioLibraryGetSongs.SimpleFilter? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Track),
        includeSingles: Boolean? = null,
        singlesOnly: Boolean? = null,
        allRoles: Boolean? = null,
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
            sort = sort,
            simpleFilter = simpleFilter,
            filter = filter,
            includeSingles = includeSingles,
            singlesOnly = singlesOnly,
            allRoles = allRoles,
        )
    ).resultOrNull?.songs

    suspend fun listTvShows(
        filter: ListFilter<ListFilterFieldsTvShows>? = null,
        sort: ListSort? = ListSort(method = ListSort.Method.Title),
        simpleFilter: VideoLibraryGetTvShows.SimpleFilter? = null,
    ): List<VideoDetailsTvShow>? = jsonEngine.post(
        VideoLibraryGetTvShows(
            properties = listOf(
                VideoFieldsTvShow.Art,
                VideoFieldsTvShow.Fanart,
                VideoFieldsTvShow.Episode,
                VideoFieldsTvShow.Genre,
                VideoFieldsTvShow.PlayCount,
                VideoFieldsTvShow.Plot,
                VideoFieldsTvShow.Rating,
                VideoFieldsTvShow.Season,
                VideoFieldsTvShow.Tag,
                VideoFieldsTvShow.Thumbnail,
                VideoFieldsTvShow.Title,
                VideoFieldsTvShow.Votes,
                VideoFieldsTvShow.WatchedEpisodes,
                VideoFieldsTvShow.Year,
            ),
            sort = sort,
            filter = filter,
            simpleFilter = simpleFilter,
        )
    ).resultOrNull?.tvshows

    fun mergePlayerProperties(value: PlayerPropertyValue) {
        _playerProperties.value = _playerProperties.value?.merge(value) ?: value
    }

    suspend fun openSubtitleSearch() = jsonEngine.post(GuiActivateWindow(window = GuiWindow.SubtitleSearch))

    suspend fun playAlbum(albumId: Int) = jsonEngine.post(PlayerOpen.Item(albumId = albumId))

    suspend fun playEpisode(episodeId: Int) = jsonEngine.post(PlayerOpen.Item(episodeId = episodeId))

    suspend fun playerOpenPlaylist(playlistId: Int, position: Int? = null) =
        jsonEngine.post(PlayerOpen.Playlist(playlistId = playlistId, position = position))

    suspend fun playMovie(movieId: Int) = jsonEngine.post(PlayerOpen.Item(movieId = movieId))

    suspend fun playOrPause() = _playerId.value?.let {
        jsonEngine.post(PlayerPlayPause(playerId = it))
    }

    suspend fun playSong(songId: Int) = jsonEngine.post(PlayerOpen.Item(songId = songId))

    fun registerNotificationListener(listener: KodiNotificationListener) = websocketEngine.registerListener(listener)

    suspend fun removePlaylistItem(playlistId: Int, position: Int) =
        jsonEngine.post(PlaylistRemove(playlistId = playlistId, position = position))

    fun seekFinish(progress: Float) {
        setPlayerElapsedTimeFromProgress(progress.toDouble())
        _playerId.value?.also {
            launchOnIOThread {
                jsonEngine.post(PlayerSeek(playerId = it, value = PlayerSeek.Value(percentage = progress * 100f)))
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

    fun setForeground(value: Boolean) = websocketEngine.setForeground(value)

    suspend fun setFullscreen() = jsonEngine.post(GuiSetFullscreen(fullscreen = GlobalToggle.True))

    suspend fun setMovieWatched(movieId: Int, value: Boolean) = jsonEngine.post(
        VideoLibrarySetMovieDetails(
            VideoLibrarySetMovieDetails.Params(movieid = movieId, playcount = if (value) 1 else 0),
        )
    )

    suspend fun setMute(value: Boolean) = jsonEngine.post(ApplicationSetMute(mute = GlobalToggle.fromValue(value)))

    suspend fun setSubtitle(index: Int) = _playerId.value?.let {
        jsonEngine.post(PlayerSetSubtitle(playerId = it, subtitleIndex = index, enable = true))
    }

    suspend fun setTvShowWatched(tvShowId: Int, value: Boolean) = jsonEngine.post(
        VideoLibrarySetTvShowDetails(
            VideoLibrarySetTvShowDetails.Params(tvshowid = tvShowId, playcount = if (value) 1 else 0),
        )
    )

    suspend fun setVolume(value: Int, rateLimited: Boolean = false) {
        val request = ApplicationSetVolume(volume = value)

        if (rateLimited) jsonEngine.postRateLimited(request, 500L)
        else jsonEngine.post(request)
    }

    suspend fun stop() = _playerId.value?.let { jsonEngine.post(PlayerStop(playerId = it)) }

    suspend fun swapPlaylistPositions(playlistId: Int, from: Int, to: Int) =
        jsonEngine.post(PlaylistSwap(playlistId = playlistId, position1 = from, position2 = to))

    suspend fun toggleShuffle() = _playerId.value?.let {
        jsonEngine.post(PlayerSetShuffle(playerId = it, shuffle = GlobalToggle.Toggle))
    }

    fun unregisterNotificationListener(listener: KodiNotificationListener) =
        websocketEngine.unregisterListener(listener)

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

    private fun setPlayerElapsedTimeFromProgress(progress: Double) {
        _playerTotalTime.value?.also {
            _playerElapsedTime.value = it.times(progress).toLong()
        }
    }
}
