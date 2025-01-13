package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.VideoFieldsTvShow
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import java.lang.reflect.Type

class VideoLibraryGetTvShowDetails(
    val tvShowId: Int,
    val properties: List<VideoFieldsTvShow>? = emptyList(),
) : AbstractRefRequest<VideoLibraryGetTvShowDetails.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetTVShowDetails"

    override fun getParams(): Any = mapOf("tvshowid" to tvShowId, "properties" to properties)

    data class Result(val tvshowdetails: VideoDetailsTvShow?)
}
