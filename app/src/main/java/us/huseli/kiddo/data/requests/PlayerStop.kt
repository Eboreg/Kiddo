package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest

class PlayerStop(val playerId: Int) : AbstractStringRequest() {
    override val method: String = "Player.Stop"

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId)
}
