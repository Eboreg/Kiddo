package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.kiddo.data.requests.interfaces.IRequestBoolResult

class GuiSetFullscreen(val fullscreen: GlobalToggle) : IRequestBoolResult {
    override val method: String = "GUI.SetFullscreen"

    override fun getParams(): Map<String, Any?> = mapOf("fullscreen" to fullscreen)
}
