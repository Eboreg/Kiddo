package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.AbstractItemRequest
import us.huseli.kiddo.data.enums.VideoFieldsEpisode
import us.huseli.kiddo.data.requests.interfaces.IItemResult
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import java.lang.reflect.Type

class VideoLibraryGetEpisodeDetails(
    val episodeId: Int,
    override val properties: List<VideoFieldsEpisode>? = null,
) : AbstractItemRequest<VideoDetailsEpisode>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "VideoLibrary.GetEpisodeDetails"

    override fun getParams(): Map<String, Any?> {
        return super.getParams() + ("episodeid" to episodeId)
    }

    @Immutable
    data class Result(val episodedetails: VideoDetailsEpisode?) : IItemResult<VideoDetailsEpisode> {
        override val item: VideoDetailsEpisode?
            get() = episodedetails
    }
}
