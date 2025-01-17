package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.ListFilterFieldsTvShows
import us.huseli.kiddo.data.enums.VideoFieldsTvShow
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.mapOfNotNull
import java.lang.reflect.Type

class VideoLibraryGetTvShows(
    override val properties: List<VideoFieldsTvShow> = listOf(
        VideoFieldsTvShow.Art,
        VideoFieldsTvShow.Fanart,
        VideoFieldsTvShow.Episode,
        VideoFieldsTvShow.Genre,
        VideoFieldsTvShow.PlayCount,
        VideoFieldsTvShow.Plot,
        VideoFieldsTvShow.Rating,
        VideoFieldsTvShow.Season,
        VideoFieldsTvShow.Tag,
        VideoFieldsTvShow.Thumbnail,
        VideoFieldsTvShow.Title,
        VideoFieldsTvShow.Votes,
        VideoFieldsTvShow.WatchedEpisodes,
        VideoFieldsTvShow.Year,
    ),
    override val limits: ListLimits? = null,
    override val sort: ListSort? = null,
    override val filter: ListFilter<ListFilterFieldsTvShows>? = null,
    override val simpleFilter: SimpleFilter? = null,
) : AbstractListRequest<VideoDetailsTvShow>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetTVShows"

    override fun getParams(): Map<String, Any?> {
        return super.getParams()
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

    @Immutable
    data class Result(
        override val limits: ListLimitsReturned,
        val tvshows: List<VideoDetailsTvShow>?,
    ) : IListResult<VideoDetailsTvShow> {
        override val items: List<VideoDetailsTvShow>?
            get() = tvshows
    }
}
