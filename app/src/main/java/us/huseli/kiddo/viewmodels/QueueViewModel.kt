package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.kiddo.data.types.interfaces.IListItemAll
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class QueueViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel(),
    KodiNotificationListener {
    private val _imageBitmaps = MutableStateFlow<Map<String, ImageBitmap?>>(emptyMap())
    private val _playlists = MutableStateFlow<List<PlaylistGetPlaylists.ResultItem>>(emptyList())
    private val _playlistItems = MutableStateFlow<Map<Int, List<IListItemAll>>>(emptyMap())

    val currentItem = repository.playerItem
    val playlists: StateFlow<List<PlaylistGetPlaylists.ResultItem>> = _playlists.asStateFlow()

    init {
        repository.registerNotificationListener(this)
        launchOnIOThread { getPlaylists() }
    }

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

    fun flowPlaylistItems(playlistId: Int) = _playlistItems.map { it[playlistId] ?: emptyList() }

    fun playPlaylistItem(playlistId: Int, position: Int) = launchOnIOThread {
        repository.playerOpenPlaylist(playlistId = playlistId, position = position)
    }

    fun removePlaylistItem(playlistId: Int, position: Int) = launchOnIOThread {
        repository.removePlaylistItem(playlistId, position)
    }

    fun swapPlaylistPositions(playlistId: Int, from: Int?, to: Int?) {
        if (from != null && to != null && from != to) launchOnIOThread {
            repository.swapPlaylistPositions(playlistId = playlistId, from = from, to = to)
            getPlaylistItems(playlistId)
        }
    }

    private suspend fun getPlaylistItems(playlistId: Int) {
        val items = repository.listPlaylistItems(playlistId)?.items

        if (items != null) _playlistItems.value += playlistId to items
    }

    private suspend fun getPlaylists() {
        repository.listPlaylists()?.also { playlists ->
            _playlists.value = playlists
            for (playlist in playlists) {
                getPlaylistItems(playlist.playlistid)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is IHasPlaylistId) {
            launchOnIOThread { getPlaylists() }
        }
    }
}
