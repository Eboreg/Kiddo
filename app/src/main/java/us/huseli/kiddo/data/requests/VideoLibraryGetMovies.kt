package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import java.lang.reflect.Type

class VideoLibraryGetMovies(
    val properties: List<VideoFieldsMovie>,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
    val filter: Filter? = null,
) : IRequestRefResult<VideoLibraryGetMovies.Result> {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetMovies"

    override fun getParams() = mapOf(
        "properties" to properties,
        "limits" to limits,
        "sort" to sort,
        "filter" to filter,
    )

    data class Filter(
        val genreid: Int? = null,
        val genre: String? = null,
        val year: Int? = null,
        val actor: String? = null,
        val director: String? = null,
        val studio: String? = null,
        val country: String? = null,
        val setid: Int? = null,
        val set: String? = null,
        val tag: String? = null,
    )

    data class Result(
        val limits: ListLimitsReturned,
        val movies: List<VideoDetailsMovie>,
    )
}
