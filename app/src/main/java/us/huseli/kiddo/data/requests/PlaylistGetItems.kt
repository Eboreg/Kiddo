package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.data.types.ListLimitsReturned
import java.lang.reflect.Type

class PlaylistGetItems(
    val playlistId: Int,
    val properties: List<ListFieldsAll>,
) : AbstractRefRequest<PlaylistGetItems.Result>() {
    override val method: String = "Playlist.GetItems"
    override val typeOfResult: Type = Result::class.java

    override fun getParams() = mapOf("playlistid" to playlistId, "properties" to properties)

    data class Result(
        val items: List<ListItemAll>,
        val limits: ListLimitsReturned,
    )
}
