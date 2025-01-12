package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.types.VideoDetailsMovie
import java.lang.reflect.Type

class VideoLibraryGetMovieDetails(
    val movieId: Int,
    val properties: List<VideoFieldsMovie> = emptyList(),
) : AbstractRefRequest<VideoLibraryGetMovieDetails.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetMovieDetails"

    override fun getParams(): Map<String, Any?> = mapOf("movieid" to movieId, "properties" to properties)

    data class Result(val moviedetails: VideoDetailsMovie?)
}
