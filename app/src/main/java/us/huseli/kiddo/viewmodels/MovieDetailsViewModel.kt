package us.huseli.kiddo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.routing.MovieDetailsRoute
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    private val _movieId = savedStateHandle.toRoute<MovieDetailsRoute>().movieId
    private val _movieDetails = flow<VideoDetailsMovie> {
        try {
            val details = repository.getMovieDetails(_movieId)

            if (details != null) emit(details)
            else _error.value = "Could not get movie details."
        } catch (e: Throwable) {
            _error.value = "Could not get movie details: $e"
        }
    }

    val error = _error.asStateFlow()
    val movieDetails = _movieDetails.stateWhileSubscribed(VideoDetailsMovie(label = "", movieid = _movieId))
    val banner = _movieDetails.map { details ->
        (details.art?.fanart ?: details.fanart ?: details.art?.banner)
            ?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val poster = _movieDetails.map { details ->
        details.art?.poster?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()

    suspend fun getImageBitmap(path: String) = repository.getImageBitmap(path)

    fun enqueue() = launchOnIOThread {
        repository.getPlaylists()?.find { it.type == PlaylistType.Video }?.let { playlist ->
            repository.enqueueMovie(
                playlistId = playlist.playlistid,
                movieId = _movieId,
                title = _movieDetails.first().displayTitle,
            )
        }
    }

    fun play() = launchOnIOThread { repository.playMovie(_movieId) }
}
