package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.requests.interfaces.IRequestHasPlayerId
import us.huseli.kiddo.data.types.GlobalTime
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import java.lang.reflect.Type

class PlayerSeek(
    override val playerId: Int,
    val value: Value,
) : IRequestRefResult<PlayerSeek.Result>, IRequestHasPlayerId {
    override val method: String = "Player.Seek"
    override val typeOfResult: Type = Result::class.java

    override fun getParams() = mapOf("playerid" to playerId, "value" to value)

    data class Value(
        val percentage: Float? = null,
        val time: Int? = null,
        val step: Step? = null,
    )

    data class Result(
        val percentage: Double?,
        val time: GlobalTime?,
        val totaltime: GlobalTime?,
    )

    @Suppress("unused")
    enum class Step {
        @SerializedName("smallforward") SmallForward,
        @SerializedName("smallbackward") SmallBackward,
        @SerializedName("bigforward") BigForward,
        @SerializedName("bigbackward") BigBackward,
    }
}
