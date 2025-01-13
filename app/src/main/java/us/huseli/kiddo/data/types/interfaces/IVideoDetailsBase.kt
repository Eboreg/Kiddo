package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.MediaArtwork

interface IVideoDetailsBase : IMediaDetailsBase {
    val art: MediaArtwork?
    val playcount: Int?

    val isWatched: Boolean
        get() = playcount?.let { it > 0 } == true
}
