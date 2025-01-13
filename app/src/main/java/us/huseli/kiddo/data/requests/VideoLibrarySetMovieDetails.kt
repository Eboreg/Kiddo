package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest
import us.huseli.kiddo.data.types.MediaArtwork
import us.huseli.kiddo.data.types.VideoRating
import us.huseli.kiddo.data.types.VideoResume

class VideoLibrarySetMovieDetails(val params: Params) : AbstractStringRequest() {
    override val method: String = "VideoLibrary.SetMovieDetails"

    override fun getParams(): Any = params

    data class Params(
        val movieid: Int,
        val art: MediaArtwork? = null,
        val country: List<String>? = null,
        val dateadded: String? = null,
        val director: List<String>? = null,
        val fanart: String? = null,
        val genre: List<String>? = null,
        val imdbnumber: String? = null,
        val lastplayed: String? = null,
        val mpaa: String? = null,
        val originaltitle: String? = null,
        val playcount: Int? = null,
        val plot: String? = null,
        val plotoutline: String? = null,
        val premiered: String? = null,
        val rating: Double? = null,
        val ratings: VideoRating? = null,
        val resume: VideoResume? = null,
        val runtime: Int? = null,
        val set: String? = null,
        val showlink: List<String>? = null,
        val sorttitle: String? = null,
        val studio: List<String>? = null,
        val tag: List<String>? = null,
        val tagline: String? = null,
        val thumbnail: String? = null,
        val title: String? = null,
        val top250: Int? = null,
        val trailer: String? = null,
        val uniqueid: Map<String, String>? = null,
        val userrating: Int? = null,
        val votes: String? = null,
        val writer: List<String>? = null,
        val year: Int? = null,
    )
}
