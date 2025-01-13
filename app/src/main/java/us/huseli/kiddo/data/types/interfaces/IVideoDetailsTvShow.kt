package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.VideoCast
import us.huseli.retaintheme.extensions.takeIfNotBlank

interface IVideoDetailsTvShow : IVideoDetailsItem {
    val cast: List<VideoCast>?
    val episode: Int?
    val episodeguide: String?
    val genre: List<String>?
    val imdbnumber: String?
    val mpaa: String?
    val originaltitle: String?
    val premiered: String?
    val rating: Double?
    val ratings: Any?
    val runtime: Int?
    val season: Int?
    val sorttitle: String?
    val status: String?
    val studio: List<String>?
    val tag: List<String>?
    val tvshowid: Int
    val uniqueid: Map<String, String>?
    val userrating: Int?
    val votes: String?
    val watchedepisodes: Int?
    val year: Int?

    val displayTitle: String
        get() = title?.takeIfNotBlank() ?: label

    val progress: Float
        get() = episode?.takeIf { it > 0 }?.let { (watchedepisodes?.toFloat() ?: 0f) / it } ?: 0f
}
