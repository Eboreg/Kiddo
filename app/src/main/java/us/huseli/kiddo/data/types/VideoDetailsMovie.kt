package us.huseli.kiddo.data.types

import us.huseli.kiddo.AbstractListMembers
import us.huseli.kiddo.data.types.interfaces.IListItemBase
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsFile
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsMovie
import us.huseli.kiddo.sensibleFormat
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.takeIfNotEmpty
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class VideoDetailsMovie(
    override val label: String,
    override val movieid: Int,
    override val art: MediaArtwork? = null,
    override val cast: List<IListItemBase.VideoCast>? = null,
    override val country: List<String>? = null,
    override val dateadded: String? = null,
    override val director: List<String>? = null,
    override val fanart: String? = null,
    override val file: String? = null,
    override val genre: List<String>? = null,
    override val imdbnumber: String? = null,
    override val lastplayed: String? = null,
    override val mpaa: String? = null,
    override val originaltitle: String? = null,
    override val playcount: Int? = null,
    override val plot: String? = null,
    override val plotoutline: String? = null,
    override val premiered: String? = null,
    override val rating: Double? = null,
    override val ratings: Map<String, VideoRating>? = null,
    override val resume: IVideoDetailsFile.VideoResume? = null,
    override val runtime: Int? = null,
    override val set: String? = null,
    override val setid: Int? = null,
    override val showlink: List<String>? = null,
    override val sorttitle: String? = null,
    override val streamdetails: IVideoDetailsFile.VideoStreams? = null,
    override val studio: List<String>? = null,
    override val tag: List<String>? = null,
    override val tagline: String? = null,
    override val thumbnail: String? = null,
    override val title: String? = null,
    override val top250: Int? = null,
    override val trailer: String? = null,
    override val uniqueid: String? = null,
    override val userrating: Int? = null,
    override val votes: String? = null,
    override val writer: List<String>? = null,
    override val year: Int? = null,
) : IVideoDetailsMovie, AbstractListMembers() {
    val displayTitle: String
        get() = title?.takeIfNotBlank() ?: label

    val supportingContent: String?
        get() = listOfNotNull(
            runtime?.toDuration(DurationUnit.SECONDS)?.sensibleFormat(withSeconds = false),
            year?.takeIf { it > 0 }?.toString(),
        ).takeIfNotEmpty()?.joinToString(" · ")

    override fun toString(): String = memberPropertiesToString()
}
