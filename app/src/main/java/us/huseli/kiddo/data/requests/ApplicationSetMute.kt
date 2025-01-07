package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.requests.interfaces.IRequestBoolResult

class ApplicationSetMute(val mute: GlobalToggle) : IRequestBoolResult {
    override val method: String = "Application.SetMute"

    override fun getParams(): Map<String, Any?> = mapOf("mute" to mute)
}
