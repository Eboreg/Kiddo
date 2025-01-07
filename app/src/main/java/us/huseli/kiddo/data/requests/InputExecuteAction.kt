package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.InputAction
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class InputExecuteAction(val action: InputAction) : IRequestStringResult {
    override val method: String = "Input.ExecuteAction"

    override fun getParams(): Map<String, Any?> = mapOf("action" to action)
}
