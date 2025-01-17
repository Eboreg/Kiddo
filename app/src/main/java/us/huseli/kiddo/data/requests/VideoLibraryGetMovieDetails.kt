package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractItemRequest
import us.huseli.kiddo.data.enums.VideoFieldsMovie
import us.huseli.kiddo.data.requests.interfaces.IItemResult
import us.huseli.kiddo.data.types.VideoDetailsMovie
import java.lang.reflect.Type

class VideoLibraryGetMovieDetails(
    val movieId: Int,
    override val properties: List<VideoFieldsMovie> = listOf(
        VideoFieldsMovie.Art,
        VideoFieldsMovie.Cast,
        VideoFieldsMovie.Country,
        VideoFieldsMovie.Director,
        VideoFieldsMovie.Fanart,
        VideoFieldsMovie.File,
        VideoFieldsMovie.Genre,
        VideoFieldsMovie.LastPlayed,
        VideoFieldsMovie.OriginalTitle,
        VideoFieldsMovie.PlayCount,
        VideoFieldsMovie.Plot,
        VideoFieldsMovie.PlotOutline,
        VideoFieldsMovie.Rating,
        VideoFieldsMovie.Resume,
        VideoFieldsMovie.Runtime,
        VideoFieldsMovie.Tag,
        VideoFieldsMovie.Tagline,
        VideoFieldsMovie.Thumbnail,
        VideoFieldsMovie.Title,
        VideoFieldsMovie.Votes,
        VideoFieldsMovie.Year,
    ),
) : AbstractItemRequest<VideoDetailsMovie>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetMovieDetails"

    override fun getParams(): Map<String, Any?> = super.getParams() + ("movieid" to movieId)

    data class Result(val moviedetails: VideoDetailsMovie?) : IItemResult<VideoDetailsMovie> {
        override val item: VideoDetailsMovie?
            get() = moviedetails
    }
}
