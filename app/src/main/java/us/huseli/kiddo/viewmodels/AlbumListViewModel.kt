package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsAlbum
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractItemListViewModel<AudioDetailsAlbum>() {
    private val _covers = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())
    private val _route = MutableStateFlow<Routes.AlbumList>(
        savedStateHandle.toRoute<Routes.AlbumList>()
    )

    val albums = items
    val listSort = _route.map { it.getListSort() }.stateWhileSubscribed()
    val route = _route.asStateFlow()

    suspend fun getCover(album: IAudioDetailsAlbum): ImageBitmap? {
        if (_covers.value.containsKey(album.albumid)) return _covers.value[album.albumid]
        return album.art?.thumb?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
            .also { _covers.value += album.albumid to it }
    }

    override fun getItemsFlow(): Flow<List<AudioDetailsAlbum>> {
        return _route.map { route ->
            repository.listAlbums(filter = route.getFilter(), sort = route.getListSort()) ?: emptyList()
        }
    }
}
