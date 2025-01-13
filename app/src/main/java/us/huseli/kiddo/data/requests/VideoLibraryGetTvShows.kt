package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ListFilterFieldsTvShows
import us.huseli.kiddo.data.enums.VideoFieldsTvShow
import us.huseli.kiddo.data.requests.interfaces.IListFilterRequest
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.mapOfNotNull
import java.lang.reflect.Type

class VideoLibraryGetTvShows(
    val properties: List<VideoFieldsTvShow>,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
    override val filter: ListFilter<ListFilterFieldsTvShows>? = null,
    override val simpleFilter: SimpleFilter? = null,
) : AbstractRefRequest<VideoLibraryGetTvShows.Result>(),
    IListFilterRequest<ListFilter<ListFilterFieldsTvShows>, VideoLibraryGetTvShows.SimpleFilter> {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetTVShows"

    override fun getParams(): Any {
        return mapOf(
            "properties" to properties,
            "limits" to limits,
            "sort" to sort,
            "filter" to getFilterParams(),
        )
    }

    data class SimpleFilter(
        val genreId: Int? = null,
        val genre: String? = null,
        val year: Int? = null,
        val actor: String? = null,
        val studio: String? = null,
        val tag: String? = null,
    ) : ISimpleFilter {
        override fun getParams(): Map<String, Any> {
            return mapOfNotNull(
                "genreid" to genreId,
                "genre" to genre,
                "year" to year,
                "actor" to actor,
                "studio" to studio,
                "tag" to tag,
            )
        }
    }

    data class Result(
        val limits: ListLimitsReturned,
        val tvshows: List<VideoDetailsTvShow>?,
    )
}
