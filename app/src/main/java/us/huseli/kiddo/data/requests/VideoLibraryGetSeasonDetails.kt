package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.VideoFieldsSeason
import us.huseli.kiddo.data.types.VideoDetailsSeason
import java.lang.reflect.Type

class VideoLibraryGetSeasonDetails(
    val seasonid: Int,
    val properties: List<VideoFieldsSeason>? = null,
) : AbstractRefRequest<VideoLibraryGetSeasonDetails.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetSeasonDetails"

    override fun getParams(): Any {
        return mapOf("seasonid" to seasonid, "properties" to properties)
    }

    data class Result(val seasondetails: VideoDetailsSeason?)
}
