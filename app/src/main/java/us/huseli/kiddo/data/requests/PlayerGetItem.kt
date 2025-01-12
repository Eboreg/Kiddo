package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.types.ListItemAll
import java.lang.reflect.Type

class PlayerGetItem(
    val playerId: Int,
    val properties: List<ListFieldsAll>,
) : AbstractRefRequest<PlayerGetItem.Result>() {
    override val method: String = "Player.GetItem"
    override val typeOfResult: Type = Result::class.java

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId, "properties" to properties)

    data class Result(val item: ListItemAll)
}
