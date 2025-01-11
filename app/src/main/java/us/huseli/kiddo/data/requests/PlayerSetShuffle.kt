package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlayerSetShuffle(val playerId: Int, val shuffle: GlobalToggle) : IRequestStringResult {
    override val method: String = "Player.SetShuffle"

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId, "shuffle" to shuffle)
}
