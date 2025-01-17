package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.mapOfNotNull
import java.lang.reflect.Type

class VideoLibraryGetMovies(
    override val properties: List<VideoFieldsMovie> = listOf(
        VideoFieldsMovie.Art,
        VideoFieldsMovie.Director,
        VideoFieldsMovie.File,
        VideoFieldsMovie.Genre,
        VideoFieldsMovie.LastPlayed,
        VideoFieldsMovie.PlayCount,
        VideoFieldsMovie.Rating,
        VideoFieldsMovie.Resume,
        VideoFieldsMovie.Runtime,
        VideoFieldsMovie.Tagline,
        VideoFieldsMovie.Title,
        VideoFieldsMovie.Year,
    ),
    override val limits: ListLimits? = null,
    override val sort: ListSort? = null,
    override val simpleFilter: SimpleFilter? = null,
    override val filter: ListFilter<ListFilterFieldsMovies>? = null,
) : AbstractListRequest<VideoDetailsMovie>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetMovies"

    override fun getParams(): Map<String, Any?> {
        return super.getParams()
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
        override val limits: ListLimitsReturned,
        val movies: List<VideoDetailsMovie>?,
    ) : IListResult<VideoDetailsMovie> {
        override val items: List<VideoDetailsMovie>?
            get() = movies
    }
}
