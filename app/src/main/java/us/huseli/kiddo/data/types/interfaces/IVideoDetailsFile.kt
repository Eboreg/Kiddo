package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.VideoResume
import us.huseli.kiddo.data.types.VideoStreams
import us.huseli.retaintheme.extensions.takeIfNotEmpty

interface IVideoDetailsFile : IVideoDetailsItem {
    val director: List<String>?
    val resume: VideoResume?
    val runtime: Int?
    val streamdetails: VideoStreams?

    val directorString: String?
        get() = director?.takeIfNotEmpty()?.joinToString(", ")

    val progress: Float
        get() = playcount?.takeIf { it > 0 }?.let { 1f } ?: resume?.progress ?: 0f
}
