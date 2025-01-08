package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import us.huseli.kiddo.KodiNotificationListener
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.PlaylistWithItems
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel(),
    KodiNotificationListener {
    val currentItem = repository.playerItem
    val playlists: StateFlow<Map<PlaylistType, PlaylistWithItems>> = repository.playlists

    init {
        repository.addNotificationListener(this)
        launchOnIOThread { repository.fetchPlaylists() }
    }

    suspend fun getImageBitmap(path: String) = repository.getImageBitmap(path)

    suspend fun listPlaylistItems(playlistId: Int) = repository.listPlaylistItems(playlistId)

    fun playPlaylistItem(playlistId: Int, position: Int) = launchOnIOThread {
        repository.playerOpenPlaylist(playlistId = playlistId, position = position)
    }

    fun removePlaylistItem(playlistId: Int, position: Int) = launchOnIOThread {
        repository.removePlaylistItem(playlistId, position)
    }

    fun swapPlaylistPositions(playlistId: Int, position1: Int, position2: Int) = launchOnIOThread {
        repository.swapPlaylistPositions(playlistId = playlistId, position1 = position1, position2 = position2)
    }

    override fun onCleared() {
        super.onCleared()
        repository.removeNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.method.startsWith("Playlist.")) {
            launchOnIOThread { repository.fetchPlaylists() }
        }
    }
}
