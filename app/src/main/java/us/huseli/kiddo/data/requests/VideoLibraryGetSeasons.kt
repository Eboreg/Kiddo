package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.VideoFieldsSeason
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsSeason
import java.lang.reflect.Type

class VideoLibraryGetSeasons(
    val tvShowId: Int? = null,
    val properties: List<VideoFieldsSeason>? = null,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
) : AbstractRefRequest<VideoLibraryGetSeasons.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetSeasons"

    override fun getParams(): Any {
        return mapOf(
            "tvshowid" to tvShowId,
            "properties" to properties,
            "limits" to limits,
            "sort" to sort,
        )
    }

    data class Result(
        val limits: ListLimitsReturned,
        val seasons: List<VideoDetailsSeason>?,
    )
}
