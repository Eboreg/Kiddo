package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsTvShow

@Immutable
data class VideoDetailsTvShow(
    override val tvshowid: Int,
    override val label: String,
    override val art: MediaArtwork? = null,
    override val cast: List<VideoCast>? = null,
    override val dateadded: String? = null,
    override val episode: Int? = null,
    override val episodeguide: String? = null,
    override val fanart: String? = null,
    override val file: String? = null,
    override val genre: List<String>? = null,
    override val imdbnumber: String? = null,
    override val lastplayed: String? = null,
    override val mpaa: String? = null,
    override val originaltitle: String? = null,
    override val playcount: Int? = null,
    override val plot: String? = null,
    override val premiered: String? = null,
    override val rating: Double? = null,
    override val ratings: Any? = null,
    override val runtime: Int? = null,
    override val season: Int? = null,
    override val sorttitle: String? = null,
    override val status: String? = null,
    override val studio: List<String>? = null,
    override val tag: List<String>? = null,
    override val thumbnail: String? = null,
    override val title: String? = null,
    override val uniqueid: Map<String, String>? = null,
    override val userrating: Int? = null,
    override val votes: String? = null,
    override val watchedepisodes: Int? = null,
    override val year: Int? = null,
) : IVideoDetailsTvShow
