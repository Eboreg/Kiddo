package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsSeason

@Immutable
data class VideoDetailsSeason(
    override val season: Int,
    override val seasonid: Int,
    override val label: String,
    override val art: MediaArtwork? = null,
    override val episode: Int? = null,
    override val fanart: String? = null,
    override val playcount: Int? = null,
    override val showtitle: String? = null,
    override val thumbnail: String? = null,
    override val title: String? = null,
    override val tvshowid: Int? = null,
    override val userrating: Int? = null,
    override val watchedepisodes: Int? = null,
) : IVideoDetailsSeason
