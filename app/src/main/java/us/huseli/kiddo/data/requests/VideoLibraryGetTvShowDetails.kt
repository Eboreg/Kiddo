package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractItemRequest
import us.huseli.kiddo.data.enums.VideoFieldsTvShow
import us.huseli.kiddo.data.requests.interfaces.IItemResult
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import java.lang.reflect.Type

class VideoLibraryGetTvShowDetails(
    val tvShowId: Int,
    override val properties: List<VideoFieldsTvShow>? = listOf(
        VideoFieldsTvShow.Art,
        VideoFieldsTvShow.Cast,
        VideoFieldsTvShow.Episode,
        VideoFieldsTvShow.Fanart,
        VideoFieldsTvShow.Genre,
        VideoFieldsTvShow.OriginalTitle,
        VideoFieldsTvShow.PlayCount,
        VideoFieldsTvShow.Plot,
        VideoFieldsTvShow.Rating,
        VideoFieldsTvShow.Runtime,
        VideoFieldsTvShow.Season,
        VideoFieldsTvShow.Tag,
        VideoFieldsTvShow.Thumbnail,
        VideoFieldsTvShow.Title,
        VideoFieldsTvShow.Votes,
        VideoFieldsTvShow.WatchedEpisodes,
        VideoFieldsTvShow.Year,
    ),
) : AbstractItemRequest<VideoDetailsTvShow>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetTVShowDetails"

    override fun getParams(): Map<String, Any?> = super.getParams() + ("tvshowid" to tvShowId)

    data class Result(val tvshowdetails: VideoDetailsTvShow?) : IItemResult<VideoDetailsTvShow> {
        override val item: VideoDetailsTvShow?
            get() = tvshowdetails
    }
}
