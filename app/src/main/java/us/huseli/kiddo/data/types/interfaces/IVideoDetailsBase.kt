package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.MediaArtwork

interface IVideoDetailsBase : IMediaDetailsBase {
    val art: MediaArtwork?
    val playcount: Int?
}
