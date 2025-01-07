package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.GuiWindow
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class GuiActivateWindow(val window: GuiWindow, val parameters: List<String>? = null) : IRequestStringResult {
    override val method: String = "GUI.ActivateWindow"

    override fun getParams(): Map<String, Any?> = mapOf("window" to window, "parameters" to parameters)
}
