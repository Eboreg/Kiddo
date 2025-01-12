package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.interfaces.IHasPlayerTime
import us.huseli.kiddo.data.interfaces.IHasPlayerTotalTime
import us.huseli.kiddo.data.types.GlobalTime
import java.lang.reflect.Type

class PlayerSeek(
    val playerId: Int,
    val value: Value,
) : AbstractRefRequest<PlayerSeek.Result>() {
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
        override val time: GlobalTime?,
        override val totaltime: GlobalTime?,
    ) : IHasPlayerTotalTime, IHasPlayerTime

    @Suppress("unused")
    enum class Step {
        @SerializedName("smallforward") SmallForward,
        @SerializedName("smallbackward") SmallBackward,
        @SerializedName("bigforward") BigForward,
        @SerializedName("bigbackward") BigBackward,
    }
}
