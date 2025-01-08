package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import us.huseli.kiddo.data.types.ListFilterMovies
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.takeIfNotEmpty
import us.huseli.retaintheme.extensions.filterValuesNotNull
import java.lang.reflect.Type

class VideoLibraryGetMovies(
    val properties: List<VideoFieldsMovie>,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
    val simpleFilter: SimpleFilter? = null,
    val filter: ListFilterMovies? = null,
) : IRequestRefResult<VideoLibraryGetMovies.Result> {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetMovies"

    override fun getParams(): Map<String, Any?> {
        val filterMap = filter?.getParams()
        val simpleFilterMap = simpleFilter?.getParams()
        val combinedFilterMap = listOfNotNull(filterMap, simpleFilterMap)
            .takeIfNotEmpty()
            ?.reduce { acc, m -> acc + m }
            ?.takeIfNotEmpty()

        return mapOf(
            "properties" to properties,
            "limits" to limits,
            "sort" to sort,
            "filter" to combinedFilterMap,
        )
    }

    /* N.B.: Using simple filter for director did not work at all 2025-01-08 (JSONRPC v13.5.0). */
    data class SimpleFilter(
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
    ) {
        fun getParams(): Map<String, Any> {
            return mapOf(
                "genreid" to genreid,
                "genre" to genre,
                "year" to year,
                "actor" to actor,
                "director" to director,
                "country" to country,
                "setid" to setid,
                "set" to set,
                "tag" to tag,
            ).filterValuesNotNull()
        }
    }

    data class Result(
        val limits: ListLimitsReturned,
        val movies: List<VideoDetailsMovie>,
    )
}
