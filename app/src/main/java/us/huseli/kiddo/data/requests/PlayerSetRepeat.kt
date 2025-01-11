package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlayerSetRepeat(val playerId: Int, val repeat: Repeat) : IRequestStringResult {
    override val method: String = "Player.SetRepeat"

    override fun getParams(): Map<String, Any?> = mapOf("playerid" to playerId, "repeat" to repeat)

    @Suppress("unused")
    enum class Repeat {
        @SerializedName("off") Off,
        @SerializedName("one") One,
        @SerializedName("all") All,
        @SerializedName("cycle") Cycle,
    }
}
