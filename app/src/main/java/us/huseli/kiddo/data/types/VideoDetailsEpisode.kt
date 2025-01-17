package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsEpisode

@Immutable
data class VideoDetailsEpisode(
    override val episodeid: Int,
    override val label: String,
    override val art: MediaArtwork? = null,
    override val cast: VideoCast? = null,
    override val dateadded: String? = null,
    override val director: List<String>? = null,
    override val episode: Int? = null,
    override val fanart: String? = null,
    override val file: String? = null,
    override val firstaired: String? = null,
    override val lastplayed: String? = null,
    override val originaltitle: String? = null,
    override val playcount: Int? = null,
    override val plot: String? = null,
    override val productioncode: String? = null,
    override val rating: Double? = null,
    override val ratings: Any? = null,
    override val resume: VideoResume? = null,
    override val runtime: Int? = null,
    override val season: Int? = null,
    override val seasonid: Int? = null,
    override val showtitle: String? = null,
    override val specialsortepisode: Int? = null,
    override val specialsortseason: Int? = null,
    override val streamdetails: VideoStreams? = null,
    override val thumbnail: String? = null,
    override val title: String? = null,
    override val tvshowid: Int? = null,
    override val uniqueid: Map<String, String>? = null,
    override val userrating: Int? = null,
    override val votes: String? = null,
    override val writer: List<String>? = null,
) : IVideoDetailsEpisode
