package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class InputSendText(val text: String, val done: Boolean? = null) : IRequestStringResult {
    override val method: String = "Input.SendText"

    override fun getParams(): Map<String, Any?> = mapOf("text" to text, "done" to done)
}
