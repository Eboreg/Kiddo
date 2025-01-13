package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ListFilterFieldsEpisodes
import us.huseli.kiddo.data.enums.VideoFieldsEpisode
import us.huseli.kiddo.data.requests.interfaces.IListFilterRequest
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.mapOfNotNull
import java.lang.reflect.Type

class VideoLibraryGetEpisodes(
    val tvShowId: Int? = null,
    val season: Int? = null,
    val properties: List<VideoFieldsEpisode>? = null,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
    override val filter: ListFilter<ListFilterFieldsEpisodes>? = null,
    override val simpleFilter: SimpleFilter? = null,
) : AbstractRefRequest<VideoLibraryGetEpisodes.Result>(),
    IListFilterRequest<ListFilter<ListFilterFieldsEpisodes>, VideoLibraryGetEpisodes.SimpleFilter> {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetEpisodes"

    override fun getParams(): Any {
        if (simpleFilter?.genreId != null || simpleFilter?.genre != null || simpleFilter?.actor != null) {
            requireNotNull(tvShowId) { "genreId, genre, & actor filtering requires tvShowId != null" }
        }

        return mapOf(
            "tvshowid" to tvShowId,
            "season" to season,
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
        val director: String? = null,
    ) : ISimpleFilter {
        override fun getParams() = mapOfNotNull(
            "genreid" to genreId,
            "genre" to genre,
            "year" to year,
            "actor" to actor,
            "director" to director,
        )
    }

    data class Result(val episodes: List<VideoDetailsEpisode>?, val limits: ListLimitsReturned)
}
