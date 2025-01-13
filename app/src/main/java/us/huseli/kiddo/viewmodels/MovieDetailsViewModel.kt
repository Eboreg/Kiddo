package us.huseli.kiddo.viewmodels

import android.content.Intent
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.KodiNotificationListener
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.VideoLibraryOnUpdate
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    override val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractSafeLoaderViewModel<VideoDetailsMovie>(), KodiNotificationListener {
    private val _imageBitmaps = MutableStateFlow<Map<String, ImageBitmap?>>(emptyMap())
    private val _movieId = savedStateHandle.toRoute<Routes.MovieDetails>().movieId

    val movieDetails = data.filterNotNull().stateWhileSubscribed(VideoDetailsMovie(label = "", movieid = _movieId))
    val banner = movieDetails.map { details ->
        val path = details.art?.fanart?.takeIfNotBlank()
            ?: details.fanart?.takeIfNotBlank()
            ?: details.art?.banner?.takeIfNotBlank()

        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val poster = movieDetails.map { details ->
        details.art?.poster?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()

    @OptIn(ExperimentalCoroutinesApi::class)
    val streamIntent: StateFlow<Intent?> = data.map { it?.file }
        .filterNotNull()
        .map { repository.flowStreamIntent(it, "video/*") }
        .flattenConcat()
        .stateWhileSubscribed()

    init {
        repository.registerNotificationListener(this)
        launchOnIOThread { loadData() }
    }

    fun enqueue() = launchOnIOThread {
        repository.enqueueVideo(PlaylistItem(movieId = movieDetails.value.movieid), movieDetails.value.displayTitle)
    }

    fun flowImageBitmap(path: String?) = _imageBitmaps.map { imageBitmaps ->
        if (path == null) null
        else if (imageBitmaps.containsKey(path)) imageBitmaps[path]
        else repository.getImageBitmap(path).also { _imageBitmaps.value += path to it }
    }.stateWhileSubscribed()

    fun play() = launchOnIOThread { repository.playMovie(_movieId) }

    fun setWatched(value: Boolean) = launchOnIOThread { repository.setMovieWatched(_movieId, value) }

    override suspend fun getData(): VideoDetailsMovie? {
        return repository.getMovieDetails(_movieId)
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is VideoLibraryOnUpdate && notification.data.item.id == _movieId) {
            launchOnIOThread { loadData() }
        }
    }
}
