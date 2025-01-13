package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.VideoFieldsEpisode
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import java.lang.reflect.Type

class VideoLibraryGetEpisodeDetails(
    val episodeId: Int,
    val properties: List<VideoFieldsEpisode>? = null,
) : AbstractRefRequest<VideoLibraryGetEpisodeDetails.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetEpisodeDetails"

    override fun getParams(): Any {
        return mapOf("episodeid" to episodeId, "properties" to properties)
    }

    data class Result(val episodedetails: List<VideoDetailsEpisode>?)
}
