package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest

class PlaylistSwap(
    val playlistId: Int,
    val position1: Int,
    val position2: Int,
) : AbstractStringRequest() {
    override val method: String = "Playlist.Swap"

    override fun getParams(): Map<String, Any?> =
        mapOf("playlistid" to playlistId, "position1" to position1, "position2" to position2)
}
