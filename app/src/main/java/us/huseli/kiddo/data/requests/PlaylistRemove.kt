package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlaylistRemove(
    val playlistId: Int,
    val position: Int,
) : IRequestStringResult {
    override val method: String = "Playlist.Remove"

    override fun getParams(): Map<String, Any?> = mapOf("playlistid" to playlistId, "position" to position)
}
