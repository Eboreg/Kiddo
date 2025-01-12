package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest
import us.huseli.kiddo.data.enums.GuiWindow

class GuiActivateWindow(val window: GuiWindow, val parameters: List<String>? = null) : AbstractStringRequest() {
    override val method: String = "GUI.ActivateWindow"

    override fun getParams(): Map<String, Any?> = mapOf("window" to window, "parameters" to parameters)
}
