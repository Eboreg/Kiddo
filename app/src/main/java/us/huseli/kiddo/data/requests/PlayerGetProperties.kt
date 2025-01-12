package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.PlayerPropertyName
import us.huseli.kiddo.data.types.PlayerPropertyValue
import java.lang.reflect.Type

class PlayerGetProperties(
    val playerId: Int,
    val properties: List<PlayerPropertyName>,
) : AbstractRefRequest<PlayerPropertyValue>() {
    override val method: String = "Player.GetProperties"
    override val typeOfResult: Type = PlayerPropertyValue::class.java

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId, "properties" to properties)
}
