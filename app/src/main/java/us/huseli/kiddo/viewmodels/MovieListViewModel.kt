package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.routing.MovieListDestination
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.ListFilterMovies
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel() {
    private val _route = MutableStateFlow<MovieListDestination.Route>(
        savedStateHandle.toRoute<MovieListDestination.Route>()
    )
    private val _filter: Flow<ListFilterMovies?> = _route.map { it.getFilter() }
    private val _movies: Flow<List<VideoDetailsMovie>> = _filter.map { filter ->
        repository.listMovies(filter = filter) ?: emptyList()
    }

    val movies = _movies.stateWhileSubscribed(emptyList())
    val route = _route.asStateFlow()
    val currentItem = repository.playerItem

    suspend fun getPoster(movie: VideoDetailsMovie): ImageBitmap? {
        return movie.art?.poster?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }
}
