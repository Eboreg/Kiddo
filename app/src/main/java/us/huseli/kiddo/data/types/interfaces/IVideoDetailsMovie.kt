package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.VideoCast
import us.huseli.kiddo.data.types.VideoRating
import us.huseli.kiddo.sensibleFormat
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import kotlin.time.Duration.Companion.seconds

interface IVideoDetailsMovie : IVideoDetailsFile {
    val cast: List<VideoCast>?
    val country: List<String>?
    val genre: List<String>?
    val imdbnumber: String?
    val movieid: Int
    val mpaa: String?
    val originaltitle: String?
    val plotoutline: String?
    val premiered: String?
    val rating: Double?
    val ratings: Map<String, VideoRating>?
    val set: String?
    val setid: Int?
    val showlink: List<String>?
    val sorttitle: String?
    val studio: List<String>?
    val tag: List<String>?
    val tagline: String?
    val top250: Int?
    val trailer: String?
    val uniqueid: String?
    val userrating: Int?
    val votes: String?
    val writer: List<String>?
    val year: Int?

    val displayTitle: String
        get() = title?.takeIfNotBlank() ?: label

    val supportingContent: String?
        get() = listOfNotNull(
            runtime?.seconds?.sensibleFormat(withSeconds = false),
            year?.takeIf { it > 0 }?.toString(),
        ).takeIfNotEmpty()?.joinToString(" · ")
}
