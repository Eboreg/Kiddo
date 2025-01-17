package us.huseli.kiddo.viewmodels.loaders

import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.AudioDetailsAlbum

class AlbumDetailsStateLoader(val repository: Repository, val albumId: Int) :
    AbstractItemStateLoader<AudioDetailsAlbum>() {
    init {
        refresh()
    }

    override suspend fun getter(): AudioDetailsAlbum? = repository.getAlbumDetails(albumId)
}
