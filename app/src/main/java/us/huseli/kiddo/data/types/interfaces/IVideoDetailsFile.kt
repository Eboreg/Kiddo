package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.VideoStreams
import us.huseli.retaintheme.extensions.takeIfNotEmpty

interface IVideoDetailsFile : IVideoDetailsItem {
    val director: List<String>?
    val resume: VideoResume?
    val runtime: Int?
    val streamdetails: VideoStreams?

    data class VideoResume(
        val position: Double?,
        val total: Double?,
    )

    val directorString: String?
        get() = director?.takeIfNotEmpty()?.joinToString(", ")
}
