package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlayerStop(val playerId: Int) : IRequestStringResult {
    override val method: String = "Player.Stop"

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId)
}
