package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlayerGoTo(
    val playerId: Int,
    val to: To = To.Absolute,
    val position: Int? = null,
) : IRequestStringResult {
    override val method: String = "Player.Goto"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "playerid" to playerId,
            "to" to when (to) {
                To.Previous -> "previous"
                To.Next -> "next"
                To.Absolute -> requireNotNull(position)
            },
        )
    }

    enum class To {
        Previous,
        Next,
        Absolute,
    }
}