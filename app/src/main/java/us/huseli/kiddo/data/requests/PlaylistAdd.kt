package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest
import us.huseli.kiddo.data.types.PlaylistItem

class PlaylistAdd(
    val playlistId: Int,
    val item: PlaylistItem,
) : AbstractStringRequest() {
    override val method: String = "Playlist.Add"

    override fun getParams(): Map<String, Any?> = mapOf("playlistid" to playlistId, "item" to item.getParams())
}
