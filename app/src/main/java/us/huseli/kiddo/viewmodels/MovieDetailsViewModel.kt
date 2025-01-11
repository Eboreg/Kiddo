package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel() {
    private val _actorPortraits = MutableStateFlow<Map<String, ImageBitmap?>>(emptyMap())
    private val _error = MutableStateFlow<String?>(null)
    private val _movieId = savedStateHandle.toRoute<Routes.MovieDetails>().movieId
    private val _movieDetails = flow<VideoDetailsMovie> {
        try {
            val details = repository.getMovieDetails(_movieId)

            if (details != null) {
                _error.value = null
                emit(details)
            } else _error.value = "Could not get movie details."
        } catch (e: Throwable) {
            _error.value = "Could not get movie details: $e"
        } finally {
            _loading.value = false
        }
    }
    private val _loading = MutableStateFlow(true)

    val error = _error.asStateFlow()
    val movieDetails = _movieDetails.stateWhileSubscribed(VideoDetailsMovie(label = "", movieid = _movieId))
    val banner = _movieDetails.map { details ->
        val path = details.art?.fanart?.takeIfNotBlank()
            ?: details.fanart?.takeIfNotBlank()
            ?: details.art?.banner?.takeIfNotBlank()

        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val poster = _movieDetails.map { details ->
        details.art?.poster?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val loading = _loading.asStateFlow()

    suspend fun getActorPortrait(path: String): ImageBitmap? {
        if (_actorPortraits.value.containsKey(path)) return _actorPortraits.value[path]
        return repository.getImageBitmap(path).also { _actorPortraits.value += path to it }
    }

    fun enqueue() = launchOnIOThread { repository.enqueueMovie(_movieDetails.first()) }

    fun play() = launchOnIOThread { repository.playMovie(_movieId) }
}
