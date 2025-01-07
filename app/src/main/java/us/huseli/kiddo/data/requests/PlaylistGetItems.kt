package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.notifications.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import java.lang.reflect.Type

class PlaylistGetItems(
    override val playlistId: Int,
    val properties: List<ListFieldsAll>,
) : IRequestRefResult<PlaylistGetItems.Result>, IHasPlaylistId {
    override val method: String = "Playlist.GetItems"
    override val typeOfResult: Type = Result::class.java

    override fun getParams() = mapOf("playlistid" to playlistId, "properties" to properties)

    data class Result(
        val items: List<ListItemAll>,
        val limits: ListLimitsReturned,
    )
}
