package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestHasPlayerId
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlayerSetSubtitle(
    override val playerId: Int,
    val subtitle: Subtitle = Subtitle.Absolute,
    val subtitleIndex: Int? = null,
    val enable: Boolean? = null,
) : IRequestStringResult, IRequestHasPlayerId {
    override val method: String = "Player.SetSubtitle"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "playerid" to playerId,
            "subtitle" to when (subtitle) {
                Subtitle.Absolute -> requireNotNull(subtitleIndex)
                else -> subtitle.value
            },
            "enable" to enable,
        )
    }

    @Suppress("unused")
    enum class Subtitle(val value: String) {
        Previous("previous"),
        Next("next"),
        Off("off"),
        On("on"),
        Absolute("absolute"),
    }
}
