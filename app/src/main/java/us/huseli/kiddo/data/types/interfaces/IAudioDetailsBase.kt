package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.MediaArtwork

interface IAudioDetailsBase : IMediaDetailsBase {
    val art: MediaArtwork?
    val dateadded: String?
    val genre: List<String>?
}