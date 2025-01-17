package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.ListFilterFieldsEpisodes
import us.huseli.kiddo.data.enums.VideoFieldsEpisode
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.mapOfNotNull
import java.lang.reflect.Type

class VideoLibraryGetEpisodes(
    override val properties: List<VideoFieldsEpisode> = listOf(
        VideoFieldsEpisode.Art,
        VideoFieldsEpisode.Episode,
        VideoFieldsEpisode.FanArt,
        VideoFieldsEpisode.File,
        VideoFieldsEpisode.OriginalTitle,
        VideoFieldsEpisode.PlayCount,
        VideoFieldsEpisode.Plot,
        VideoFieldsEpisode.Rating,
        VideoFieldsEpisode.Resume,
        VideoFieldsEpisode.Runtime,
        VideoFieldsEpisode.Season,
        VideoFieldsEpisode.SeasonId,
        VideoFieldsEpisode.Thumbnail,
        VideoFieldsEpisode.Title,
        VideoFieldsEpisode.TvShowId,
    ),
    val tvShowId: Int? = null,
    val season: Int? = null,
    override val limits: ListLimits? = null,
    override val sort: ListSort? = null,
    override val filter: ListFilter<ListFilterFieldsEpisodes>? = null,
    override val simpleFilter: SimpleFilter? = null,
) : AbstractListRequest<VideoDetailsEpisode>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetEpisodes"

    override fun getParams(): Map<String, Any?> {
        if (simpleFilter?.genreId != null || simpleFilter?.genre != null || simpleFilter?.actor != null) {
            requireNotNull(tvShowId) { "genreId, genre, & actor filtering requires tvShowId != null" }
        }

        return super.getParams() + mapOf(
            "tvshowid" to tvShowId,
            "season" to season,
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

    data class Result(
        val episodes: List<VideoDetailsEpisode>?,
        override val limits: ListLimitsReturned,
    ) : IListResult<VideoDetailsEpisode> {
        override val items: List<VideoDetailsEpisode>?
            get() = episodes
    }
}
