package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.takeIfNotEmpty

interface IAudioDetailsMedia : IAudioDetailsBase {
    val artist: List<String>?
    val artistid: List<Int>?
    val displayartist: String?
    val musicbrainzalbumartistid: List<String>?
    val originaldate: String?
    val rating: Double?
    val releasedate: String?
    val sortartist: String?
    val title: String?
    val userrating: Int?
    val votes: String?
    val year: Int?

    val artistString: String?
        get() = artist?.takeIfNotEmpty()?.joinToString(", ")

    val displayTitle: String
        get() = title?.takeIfNotBlank() ?: label
}
