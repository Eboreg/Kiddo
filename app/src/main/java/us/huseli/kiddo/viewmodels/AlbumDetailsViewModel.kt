package us.huseli.kiddo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.KodiNotificationListener
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.AudioLibraryOnUpdate
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    override val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractSafeLoaderViewModel<AudioDetailsAlbum>(), KodiNotificationListener {
    private val _albumId = savedStateHandle.toRoute<Routes.AlbumDetails>().albumId

    val songs = flow<List<AudioDetailsSong>> {
        repository.listAlbumSongs(_albumId)?.also { emit(it) }
    }.stateWhileSubscribed(emptyList())
    val albumDetails =
        data.filterNotNull().stateWhileSubscribed(AudioDetailsAlbum(albumid = _albumId, label = ""))
    val cover = albumDetails.map { details ->
        val path = details.art?.thumb?.takeIfNotBlank() ?: details.thumbnail?.takeIfNotBlank()
        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val banner = albumDetails.map { details ->
        val path = details.art?.banner?.takeIfNotBlank()
            ?: details.art?.fanart?.takeIfNotBlank()
            ?: details.fanart?.takeIfNotBlank()

        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()

    init {
        repository.registerNotificationListener(this)
        launchOnIOThread { loadData() }
    }

    fun enqueue() = launchOnIOThread { repository.enqueueAlbum(albumDetails.first()) }

    fun play() = launchOnIOThread { repository.playAlbum(_albumId) }

    fun playSong(songId: Int) = launchOnIOThread { repository.playSong(songId) }

    override suspend fun getData(): AudioDetailsAlbum? {
        return repository.getAlbumDetails(_albumId)
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is AudioLibraryOnUpdate && notification.data.id == _albumId) {
            launchOnIOThread { loadData() }
        }
    }
}
