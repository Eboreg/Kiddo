package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractItemRequest
import us.huseli.kiddo.data.enums.VideoFieldsSeason
import us.huseli.kiddo.data.requests.interfaces.IItemResult
import us.huseli.kiddo.data.types.VideoDetailsSeason
import java.lang.reflect.Type

class VideoLibraryGetSeasonDetails(
    val seasonid: Int,
    override val properties: List<VideoFieldsSeason>? = null,
) : AbstractItemRequest<VideoDetailsSeason>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetSeasonDetails"

    override fun getParams(): Map<String, Any?> {
        return super.getParams() + ("seasonid" to seasonid)
    }

    data class Result(val seasondetails: VideoDetailsSeason?) : IItemResult<VideoDetailsSeason> {
        override val item: VideoDetailsSeason?
            get() = seasondetails
    }
}
