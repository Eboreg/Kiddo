package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.requests.interfaces.IListFilterRequest
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.mapOfNotNull
import java.lang.reflect.Type

class VideoLibraryGetMovies(
    val properties: List<VideoFieldsMovie>,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
    override val simpleFilter: SimpleFilter? = null,
    override val filter: ListFilter<ListFilterFieldsMovies>? = null,
) : AbstractRefRequest<VideoLibraryGetMovies.Result>(),
    IListFilterRequest<ListFilter<ListFilterFieldsMovies>, VideoLibraryGetMovies.SimpleFilter> {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetMovies"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "properties" to properties,
            "limits" to limits,
            "sort" to sort,
            "filter" to getFilterParams(),
        )
    }

    /* N.B.: Using simple filter for director did not work at all 2025-01-08 (JSONRPC v13.5.0). */
    data class SimpleFilter(
        val genreId: Int? = null,
        val genre: String? = null,
        val year: Int? = null,
        val actor: String? = null,
        val director: String? = null,
        val studio: String? = null,
        val country: String? = null,
        val setId: Int? = null,
        val set: String? = null,
        val tag: String? = null,
    ) : ISimpleFilter {
        override fun getParams(): Map<String, Any> {
            return mapOfNotNull(
                "genreid" to genreId,
                "genre" to genre,
                "year" to year,
                "actor" to actor,
                "director" to director,
                "country" to country,
                "setid" to setId,
                "set" to set,
                "tag" to tag,
            )
        }
    }

    data class Result(
        val limits: ListLimitsReturned,
        val movies: List<VideoDetailsMovie>,
    )
}
