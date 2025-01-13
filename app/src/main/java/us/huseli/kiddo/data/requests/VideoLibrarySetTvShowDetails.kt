package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.AbstractStringRequest
import us.huseli.kiddo.data.types.MediaArtwork
import us.huseli.kiddo.data.types.VideoRating

class VideoLibrarySetTvShowDetails(val params: Params) : AbstractStringRequest() {
    override val method: String = "VideoLibrary.SetTVShowDetails"

    override fun getParams(): Any = params

    data class Params(
        val tvshowid: Int,
        val art: MediaArtwork? = null,
        val dateadded: String? = null,
        val episodeguide: String? = null,
        val fanart: String? = null,
        val genre: List<String>? = null,
        val imdbnumber: String? = null,
        val lastplayed: String? = null,
        val mpaa: String? = null,
        val originaltitle: String? = null,
        val playcount: Int? = null,
        val plot: String? = null,
        val premiered: String? = null,
        val rating: Double? = null,
        val ratings: VideoRating? = null,
        val runtime: Int? = null,
        val sorttitle: String? = null,
        val status: Status? = null,
        val studio: List<String>? = null,
        val tag: List<String>? = null,
        val thumbnail: String? = null,
        val title: String? = null,
        val uniqueid: Map<String, String>? = null,
        val userrating: Int? = null,
        val votes: String? = null,
    ) {
        @Suppress("unused")
        enum class Status {
            @SerializedName("returning series") ReturningSeries,
            @SerializedName("in production") InProduction,
            @SerializedName("planned") Planned,
            @SerializedName("cancelled") Cancelled,
            @SerializedName("ended") Ended,
        }
    }
}
