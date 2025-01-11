package us.huseli.kiddo.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel() {
    private val _error = MutableStateFlow<String?>(null)
    private val _albumId = savedStateHandle.toRoute<Routes.AlbumDetails>().albumId
    private val _albumDetails = flow<AudioDetailsAlbum> {
        try {
            val details = repository.getAlbumDetails(_albumId)

            if (details != null) {
                _error.value = null
                emit(details)
            } else _error.value = "Could not get album details."
        } catch (e: Throwable) {
            _error.value = "Could not get album details: $e"
        } finally {
            _loading.value = false
        }
    }
    private val _songs = flow<List<AudioDetailsSong>> {
        repository.listAlbumSongs(_albumId)?.also { emit(it) }
    }
    private val _loading = MutableStateFlow(true)

    val albumDetails = _albumDetails.stateWhileSubscribed(AudioDetailsAlbum(albumid = _albumId, label = ""))
    val error = _error.asStateFlow()
    val loading = _loading.asStateFlow()
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
    val songs = _songs.stateWhileSubscribed(emptyList())

    fun enqueue() = launchOnIOThread { repository.enqueueAlbum(_albumDetails.first()) }

    fun play() = launchOnIOThread { repository.playAlbum(_albumId) }

    fun playSong(songId: Int) = launchOnIOThread { repository.playSong(songId) }
}
