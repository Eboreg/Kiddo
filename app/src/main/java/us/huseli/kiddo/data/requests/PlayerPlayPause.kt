package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.requests.interfaces.IRequestHasPlayerId
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import java.lang.reflect.Type

class PlayerPlayPause(
    override val playerId: Int,
    val play: GlobalToggle? = null,
) : IRequestRefResult<PlayerPlayPause.Result>, IRequestHasPlayerId {
    override val method: String = "Player.PlayPause"
    override val typeOfResult: Type = Result::class.java

    override fun getParams() = mapOf("playerid" to playerId, "play" to play)

    data class Result(override val speed: Int) : IHasPlayerSpeed
}
