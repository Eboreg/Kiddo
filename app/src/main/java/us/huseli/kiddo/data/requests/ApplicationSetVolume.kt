package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractIntRequest

class ApplicationSetVolume(val type: ParamType = ParamType.Absolute, val volume: Int? = null) : AbstractIntRequest() {
    override val method: String = "Application.SetVolume"

    override fun getParams(): Map<String, Any?> {
        return when (type) {
            ParamType.Absolute -> mapOf("volume" to requireNotNull(volume))
            ParamType.Increment -> mapOf("volume" to "increment")
            ParamType.Decrement -> mapOf("volume" to "decrement")
        }
    }

    enum class ParamType {
        Absolute,
        Increment,
        Decrement,
    }
}
