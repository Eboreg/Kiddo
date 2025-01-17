package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsAlbum
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.kiddo.paging.MediaPagingSource
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractItemListViewModel<AudioDetailsAlbum>(), KodiNotificationListener {
    val route = savedStateHandle.toRoute<Routes.AlbumList>()

    private val _covers = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())

    override val pagingSourceFactory: () -> MediaPagingSource<AudioDetailsAlbum> = {
        repository.albumPagingSource(filter = route.filter, sort = route.listSort)
    }

    init {
        repository.registerNotificationListener(this)
    }

    fun flowCover(album: IAudioDetailsAlbum) = _covers.map { covers ->
        if (covers.containsKey(album.albumid)) covers[album.albumid]
        else album.art?.thumb?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
            .also { _covers.value += album.albumid to it }
    }.stateWhileSubscribed()

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (
            listOf("AudioLibrary.OnScanFinished", "AudioLibrary.OnCleanFinished", "AudioLibrary.OnUpdate")
                .contains(notification.method)
        ) invalidateSource()
    }
}
