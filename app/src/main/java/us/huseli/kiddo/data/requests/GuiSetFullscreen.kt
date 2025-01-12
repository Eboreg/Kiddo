package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractBoolRequest
import us.huseli.kiddo.data.enums.GlobalToggle

class GuiSetFullscreen(val fullscreen: GlobalToggle) : AbstractBoolRequest() {
    override val method: String = "GUI.SetFullscreen"

    override fun getParams(): Map<String, Any?> = mapOf("fullscreen" to fullscreen)
}
