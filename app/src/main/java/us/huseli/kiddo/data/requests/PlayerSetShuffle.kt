package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest
import us.huseli.kiddo.data.enums.GlobalToggle

class PlayerSetShuffle(val playerId: Int, val shuffle: GlobalToggle) : AbstractStringRequest() {
    override val method: String = "Player.SetShuffle"

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId, "shuffle" to shuffle)
}
