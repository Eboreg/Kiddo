package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractBoolRequest
import us.huseli.kiddo.data.enums.GlobalToggle

class ApplicationSetMute(val mute: GlobalToggle) : AbstractBoolRequest() {
    override val method: String = "Application.SetMute"

    override fun getParams(): Map<String, Any?> = mapOf("mute" to mute)
}
