package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.AbstractItemRequest
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.requests.interfaces.IItemResult
import us.huseli.kiddo.data.types.ListItemAll
import java.lang.reflect.Type

class PlayerGetItem(
    val playerId: Int,
    override val properties: List<ListFieldsAll>,
) : AbstractItemRequest<ListItemAll>() {
    override val method: String = "Player.GetItem"
    override val typeOfResult: Type = Result::class.java

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId, "properties" to properties)

    @Immutable
    data class Result(override val item: ListItemAll) : IItemResult<ListItemAll>
}
