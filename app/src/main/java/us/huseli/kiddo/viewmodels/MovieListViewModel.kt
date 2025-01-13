package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.KodiNotificationListener
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsMovie
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    override val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractSafeLoaderViewModel<List<VideoDetailsMovie>>(), KodiNotificationListener {
    private val _posters = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())

    val route = savedStateHandle.toRoute<Routes.MovieList>()
    val movies = data.filterNotNull().stateWhileSubscribed(emptyList())
    val currentItem = repository.playerItem

    init {
        repository.registerNotificationListener(this)
        launchOnIOThread { loadData() }
    }

    fun flowPoster(movie: IVideoDetailsMovie) = _posters.map { posters ->
        if (posters.containsKey(movie.movieid)) posters[movie.movieid]
        else movie.art?.poster?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
            .also { _posters.value += movie.movieid to it }
    }.stateWhileSubscribed()

    override suspend fun getData(): List<VideoDetailsMovie>? {
        return repository.listMovies(filter = route.getFilter(), sort = route.getListSort())
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (
            listOf(
                "VideoLibrary.OnRefresh",
                "VideoLibrary.OnScanFinished",
                "VideoLibrary.OnCleanFinished"
            ).contains(notification.method)
        ) launchOnIOThread { loadData() }
    }
}
