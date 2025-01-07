package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestHasPlayerId
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlayerStop(override val playerId: Int) : IRequestStringResult, IRequestHasPlayerId {
    override val method: String = "Player.Stop"

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId)
}
