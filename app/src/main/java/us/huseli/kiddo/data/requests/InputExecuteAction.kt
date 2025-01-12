package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest
import us.huseli.kiddo.data.enums.InputAction

class InputExecuteAction(val action: InputAction) : AbstractStringRequest() {
    override val method: String = "Input.ExecuteAction"

    override fun getParams(): Map<String, Any?> = mapOf("action" to action)
}
