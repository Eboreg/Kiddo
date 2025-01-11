package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlaylistSwap(
    val playlistId: Int,
    val position1: Int,
    val position2: Int,
) : IRequestStringResult {
    override val method: String = "Playlist.Swap"

    override fun getParams(): Map<String, Any?> =
        mapOf("playlistid" to playlistId, "position1" to position1, "position2" to position2)
}
