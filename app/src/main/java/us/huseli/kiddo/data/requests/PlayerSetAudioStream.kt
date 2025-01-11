package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class PlayerSetAudioStream(
    val playerId: Int,
    val stream: Stream = Stream.Absolute,
    val streamIndex: Int? = null,
) : IRequestStringResult {
    override val method: String = "Player.SetAudioStream"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "playerid" to playerId,
            "stream" to when (stream) {
                Stream.Previous -> "previous"
                Stream.Next -> "next"
                Stream.Absolute -> requireNotNull(streamIndex)
            },
        )
    }

    enum class Stream {
        Previous,
        Next,
        Absolute,
    }
}
