package us.huseli.kiddo.viewmodels

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.VideoLibraryOnUpdate
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.managers.download.DownloadEvent
import us.huseli.kiddo.managers.download.DownloadListener
import us.huseli.kiddo.managers.download.DownloadProgress
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.viewmodels.loaders.MovieDetailsStateLoader
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel(), KodiNotificationListener, DownloadListener {
    private val _movieId = savedStateHandle.toRoute<Routes.MovieDetails>().movieId
    private val _loader = MovieDetailsStateLoader(repository, _movieId)
    private val _downloadProgress = MutableStateFlow<DownloadProgress?>(null)
    private val _imageBitmaps = MutableStateFlow<Map<String, ImageBitmap?>>(emptyMap())
    private val _movieDetails =
        _loader.item.filterIsInstance<ItemLoadState.Loaded<VideoDetailsMovie>>().map { it.item }
    private var _downloadCancelIntent: PendingIntent? = null

    val downloadProgress = _downloadProgress.asStateFlow()
    val movieDetailsState = _loader.item

    val listItemFile = _movieDetails
        .mapNotNull { it.file }
        .map { file -> repository.getFileDetails(file) }
        .stateWhileSubscribed()

    val banner = _movieDetails.map { details ->
        val path = details.art?.fanart?.takeIfNotBlank()
            ?: details.fanart?.takeIfNotBlank()
            ?: details.art?.banner?.takeIfNotBlank()
        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()

    val poster = _movieDetails.map { details ->
        details.art?.poster?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()

    val mimeType = _movieDetails
        .mapNotNull { it.file }
        .mapNotNull { file -> repository.getFileDetails(file)?.mimetype }
        .stateWhileSubscribed("video/*")

    @OptIn(ExperimentalCoroutinesApi::class)
    val streamIntent: StateFlow<Intent?> = _movieDetails
        .map { details ->
            details.file?.let { repository.flowStreamIntent(it, "video/*", details.displayTitle) } ?: emptyFlow()
        }
        .flattenConcat()
        .stateWhileSubscribed()

    val similar = _movieDetails.map { repository.listSimilarMovies(it, 6) }.stateWhileSubscribed(emptyList())

    init {
        repository.registerNotificationListener(this)
        repository.registerDownloadListener(this)
    }

    fun cancelDownload() {
        _downloadCancelIntent?.send()
    }

    fun download(localUri: Uri) {
        launchOnIOThread {
            val details = _movieDetails.first()

            details.file?.also { file ->
                repository.downloadFile(file, localUri, extra = details)
            }
        }
    }

    fun enqueue() = launchOnIOThread {
        val details = _movieDetails.first()
        repository.enqueueVideo(PlaylistItem(movieId = details.movieid), details.displayTitle)
    }

    fun flowImageBitmap(path: String?) = _imageBitmaps.map { imageBitmaps ->
        if (path == null) null
        else if (imageBitmaps.containsKey(path)) imageBitmaps[path]
        else repository.getImageBitmap(path).also { _imageBitmaps.value += path to it }
    }.stateWhileSubscribed()

    fun play() = launchOnIOThread { repository.playMovie(_movieId) }

    fun setWatched(value: Boolean) = launchOnIOThread { repository.setMovieWatched(_movieId, value) }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
        repository.unregisterDownloadListener(this)
    }

    override fun onDownloadEvent(event: DownloadEvent) {
        if ((event.data.extra as? VideoDetailsMovie)?.movieid == _movieId) {
            when (event) {
                is DownloadEvent.Finished, is DownloadEvent.Cancelled -> {
                    _downloadProgress.value = null
                    _downloadCancelIntent = null
                }

                is DownloadEvent.Progress -> _downloadProgress.value = event.progress
                is DownloadEvent.Started -> _downloadCancelIntent = event.cancelIntent
            }
        }
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is VideoLibraryOnUpdate && notification.data.theId() == _movieId) _loader.refresh()
    }
}
