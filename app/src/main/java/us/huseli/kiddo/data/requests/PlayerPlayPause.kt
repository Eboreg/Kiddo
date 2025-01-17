package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import java.lang.reflect.Type

class PlayerPlayPause(
    val playerId: Int,
    val play: GlobalToggle? = null,
) : AbstractRefRequest<PlayerPlayPause.Result>() {
    override val method: String = "Player.PlayPause"
    override val typeOfResult: Type = Result::class.java

    override fun getParams() = mapOf("playerid" to playerId, "play" to play)

    @Immutable
    data class Result(override val speed: Int) : IHasPlayerSpeed
}
