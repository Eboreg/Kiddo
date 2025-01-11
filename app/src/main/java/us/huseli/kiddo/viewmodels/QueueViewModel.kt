package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.transform
import us.huseli.kiddo.KodiNotificationListener
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel(),
    KodiNotificationListener {
    private val _imageBitmaps = MutableStateFlow<Map<String, ImageBitmap?>>(emptyMap())

    val currentItem = repository.playerItem
    val playlists: StateFlow<List<PlaylistGetPlaylists.ResultItem>> = repository.playlists

    init {
        repository.addNotificationListener(this)
        launchOnIOThread { repository.fetchPlaylists() }
    }

    fun flowPlaylistItems(playlistId: Int) = repository.flowPlaylistItems(playlistId)

    fun flowImageBitmap(path: String?) = _imageBitmaps.transform { map ->
        if (path != null) {
            emit(map[path])
            if (!map.containsKey(path)) {
                _imageBitmaps.value += path to null

                val image = repository.getImageBitmap(path)

                if (image != null) {
                    emit(image)
                    _imageBitmaps.value += path to image
                }
            }
        }
    }.stateWhileSubscribed()

    fun playPlaylistItem(playlistId: Int, position: Int) = launchOnIOThread {
        repository.playerOpenPlaylist(playlistId = playlistId, position = position)
    }

    fun removePlaylistItem(playlistId: Int, position: Int) = launchOnIOThread {
        repository.removePlaylistItem(playlistId, position)
    }

    fun swapPlaylistPositions(playlistId: Int, from: Int?, to: Int?) {
        if (from != null && to != null && from != to) launchOnIOThread {
            repository.swapPlaylistPositions(playlistId = playlistId, from = from, to = to)
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is IHasPlaylistId) {
            launchOnIOThread { repository.fetchPlaylists() }
        }
    }
}
