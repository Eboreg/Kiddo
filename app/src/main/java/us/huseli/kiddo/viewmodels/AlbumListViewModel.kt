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
import us.huseli.kiddo.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class AlbumListViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractItemListViewModel<AudioDetailsAlbum>() {
    private val _route = MutableStateFlow<Routes.AlbumList>(
        savedStateHandle.toRoute<Routes.AlbumList>()
    )
    private val _filter = _route.map { it.getFilter() }
    private val _covers = MutableStateFlow<Map<Int, ImageBitmap?>>(emptyMap())

    val albums = items
    val route = _route.asStateFlow()
    val listSort = _route.map { it.getListSort() }.stateWhileSubscribed()

    override fun getItemsFlow(): Flow<List<AudioDetailsAlbum>> {
        return _filter.map { filter -> repository.listAlbums(filter = filter) ?: emptyList() }
    }

    suspend fun getCover(album: IAudioDetailsAlbum): ImageBitmap? {
        if (_covers.value.containsKey(album.albumid)) return _covers.value[album.albumid]
        return album.art?.thumb?.takeIfNotBlank()
            ?.let { repository.getImageBitmap(it) }
            .also { _covers.value += album.albumid to it }
    }
}
