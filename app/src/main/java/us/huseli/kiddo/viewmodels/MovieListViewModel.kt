package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsMovie
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractItemListViewModel<VideoDetailsMovie>() {
    private val _route =
        MutableStateFlow<Routes.MovieList>(savedStateHandle.toRoute<Routes.MovieList>())
    private val _posters = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())

    val movies = items
    val route = _route.asStateFlow()
    val currentItem = repository.playerItem
    val listSort = _route.map { it.getListSort() }.stateWhileSubscribed()

    override fun getItemsFlow(): Flow<List<VideoDetailsMovie>> {
        return _route.map { route ->
            repository.listMovies(filter = route.getFilter(), sort = route.getListSort()) ?: emptyList()
        }
    }

    suspend fun getPoster(movie: IVideoDetailsMovie): ImageBitmap? {
        if (_posters.value.containsKey(movie.movieid)) return _posters.value[movie.movieid]
        return movie.art?.poster?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
            .also { _posters.value += movie.movieid to it }
    }
}
