package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest

class PlaylistRemove(
    val playlistId: Int,
    val position: Int,
) : AbstractStringRequest() {
    override val method: String = "Playlist.Remove"

    override fun getParams(): Map<String, Any?> = mapOf("playlistid" to playlistId, "position" to position)
}
