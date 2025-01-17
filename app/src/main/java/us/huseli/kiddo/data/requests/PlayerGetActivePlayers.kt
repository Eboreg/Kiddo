package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.interfaces.IHasPlayerId
import java.lang.reflect.Type

class PlayerGetActivePlayers() : AbstractRefRequest<List<PlayerGetActivePlayers.ResultItem>>() {
    override val method: String = "Player.GetActivePlayers"
    override val typeOfResult: Type = TypeToken.getParameterized(ArrayList::class.java, ResultItem::class.java).type

    override fun getParams(): Map<String, Any?> = emptyMap()

    @Immutable
    data class ResultItem(
        override val playerid: Int,
        val playertype: PlayerPlayerType,
        val type: PlayerType,
    ) : IHasPlayerId {
        @Suppress("unused")
        enum class PlayerPlayerType {
            @SerializedName("internal") Internal,
            @SerializedName("external") External,
            @SerializedName("remote") Remote,
        }

        @Suppress("unused")
        enum class PlayerType {
            @SerializedName("video") Video,
            @SerializedName("audio") Audio,
            @SerializedName("picture") Picture,
        }
    }
}
