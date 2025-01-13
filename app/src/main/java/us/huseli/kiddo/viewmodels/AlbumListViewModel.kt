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
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsAlbum
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    override val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractSafeLoaderViewModel<List<AudioDetailsAlbum>>(), KodiNotificationListener {
    private val _covers = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())
    val route = savedStateHandle.toRoute<Routes.AlbumList>()

    val albums = data.filterNotNull().stateWhileSubscribed(emptyList())

    init {
        repository.registerNotificationListener(this)
        launchOnIOThread { loadData() }
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
        if (listOf("AudioLibrary.OnScanFinished", "AudioLibrary.OnCleanFinished").contains(notification.method)) {
            launchOnIOThread { loadData() }
        }
    }

    override suspend fun getData(): List<AudioDetailsAlbum>? {
        return repository.listAlbums(filter = route.getFilter(), sort = route.getListSort())
    }
}
