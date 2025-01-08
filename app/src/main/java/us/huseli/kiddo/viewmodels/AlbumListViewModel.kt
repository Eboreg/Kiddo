package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.routing.AlbumListDestination
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel() {
    private val _route = MutableStateFlow<AlbumListDestination.Route>(
        savedStateHandle.toRoute<AlbumListDestination.Route>()
    )
    private val _filter = _route.map { it.getFilter() }
    private val _albums: Flow<List<AudioDetailsAlbum>> = _filter.map { filter ->
        repository.listAlbums(filter = filter) ?: emptyList()
    }

    val albums = _albums.stateWhileSubscribed(emptyList())

    suspend fun getCover(album: AudioDetailsAlbum): ImageBitmap? {
        return album.art?.thumb?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }
}
