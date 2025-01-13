package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsTvShow
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class TvShowListViewModel @Inject constructor(
    override val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractSafeLoaderViewModel<List<VideoDetailsTvShow>>() {
    private val _posters = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())

    val route = savedStateHandle.toRoute<Routes.TvShowList>()
    val tvShows = data.filterNotNull().stateWhileSubscribed(emptyList())

    init {
        launchOnIOThread { loadData() }
    }

    fun flowPoster(show: IVideoDetailsTvShow) = _posters.map { posters ->
        if (posters.containsKey(show.tvshowid)) posters[show.tvshowid]
        else show.art?.poster?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
            .also { _posters.value += show.tvshowid to it }
    }.stateWhileSubscribed()

    override suspend fun getData(): List<VideoDetailsTvShow>? {
        return repository.listTvShows(filter = route.getFilter(), sort = route.getListSort())
    }
}
