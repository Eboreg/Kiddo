package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestHasPlayerId
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import us.huseli.kiddo.data.types.PlayerSpeed
import java.lang.reflect.Type

class PlayerSetSpeed(
    override val playerId: Int,
    val type: ParamType,
    val speed: Int? = null,
) : IRequestRefResult<PlayerSpeed>, IRequestHasPlayerId {
    private val validSpeeds = listOf(-32, -16, -8, -4, -2, -1, 0, 1, 2, 4, 8, 16, 32)
    override val typeOfResult: Type = PlayerSpeed::class.java
    override val method: String = "Player.SetSpeed"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "playerid" to playerId,
            "speed" to when (type) {
                ParamType.Absolute -> checkSpeed()
                ParamType.Increment -> "increment"
                ParamType.Decrement -> "decrement"
            }
        )
    }

    private fun checkSpeed(): Int {
        return requireNotNull(speed).also { require(validSpeeds.contains(it)) }
    }

    enum class ParamType {
        Absolute,
        Increment,
        Decrement,
    }
}