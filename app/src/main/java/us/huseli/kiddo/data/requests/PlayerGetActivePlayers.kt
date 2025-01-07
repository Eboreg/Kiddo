package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerId
import us.huseli.kiddo.data.requests.interfaces.IRequestNoParams
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import java.lang.reflect.Type

class PlayerGetActivePlayers() : IRequestRefResult<List<PlayerGetActivePlayers.ResultItem>>,
    IRequestNoParams<List<PlayerGetActivePlayers.ResultItem>> {
    override val method: String = "Player.GetActivePlayers"
    override val typeOfResult: Type = TypeToken.getParameterized(ArrayList::class.java, ResultItem::class.java).type

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
