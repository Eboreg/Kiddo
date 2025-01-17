package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsTvShow
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.kiddo.paging.MediaPagingSource
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class TvShowListViewModel @Inject constructor(
    val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractItemListViewModel<VideoDetailsTvShow>(), KodiNotificationListener {
    private val _posters = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())

    val route = savedStateHandle.toRoute<Routes.TvShowList>()

    override val pagingSourceFactory: () -> MediaPagingSource<VideoDetailsTvShow> = {
        repository.tvShowPagingSource(filter = route.filter, sort = route.listSort)
    }

    init {
        repository.registerNotificationListener(this)
    }

    fun flowPoster(show: IVideoDetailsTvShow) = _posters.map { posters ->
        if (posters.containsKey(show.tvshowid)) posters[show.tvshowid]
        else show.art?.poster?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
            .also { _posters.value += show.tvshowid to it }
    }.stateWhileSubscribed()

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (
            listOf(
                "VideoLibrary.OnRefresh",
                "VideoLibrary.OnScanFinished",
                "VideoLibrary.OnCleanFinished",
            ).contains(notification.method)
        ) invalidateSource()
    }
}
