package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import us.huseli.kiddo.data.types.PlayerTempo
import java.lang.reflect.Type

class PlayerSetTempo(
    val playerId: Int,
    val type: ParamType,
    val tempo: Double? = null,
) : IRequestRefResult<PlayerTempo> {
    override val typeOfResult: Type = PlayerTempo::class.java
    override val method: String = "Player.SetTempo"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "playerid" to playerId,
            "tempo" to when (type) {
                ParamType.Absolute -> requireNotNull(tempo)
                ParamType.Increment -> "increment"
                ParamType.Decrement -> "decrement"
            },
        )
    }

    enum class ParamType {
        Absolute,
        Increment,
        Decrement,
    }
}
