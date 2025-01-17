package us.huseli.kiddo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.AudioLibraryOnUpdate
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.viewmodels.loaders.AlbumDetailsStateLoader
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel(), KodiNotificationListener {
    private val _albumId = savedStateHandle.toRoute<Routes.AlbumDetails>().albumId
    private val _loader = AlbumDetailsStateLoader(repository, _albumId)
    private val _albumDetails =
        _loader.item.filterIsInstance<ItemLoadState.Loaded<AudioDetailsAlbum>>().map { it.item }

    val albumDetailsState = _loader.item

    val songs = flow<List<AudioDetailsSong>> {
        repository.listAlbumSongs(_albumId)?.items?.also { emit(it) }
    }.stateWhileSubscribed(emptyList())

    val cover = _albumDetails.map { details ->
        val path = details.art?.thumb?.takeIfNotBlank() ?: details.thumbnail?.takeIfNotBlank()
        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val banner = _albumDetails.map { details ->
        val path = details.art?.banner?.takeIfNotBlank()
            ?: details.art?.fanart?.takeIfNotBlank()
            ?: details.fanart?.takeIfNotBlank()

        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()

    init {
        repository.registerNotificationListener(this)
    }

    fun enqueue() = launchOnIOThread { repository.enqueueAlbum(_albumDetails.first()) }

    fun play() = launchOnIOThread { repository.playAlbum(_albumId) }

    fun playSong(songId: Int) = launchOnIOThread { repository.playSong(songId) }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is AudioLibraryOnUpdate && notification.data.id == _albumId) _loader.refresh()
    }
}
