package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest

class InputSendText(val text: String, val done: Boolean? = null) : AbstractStringRequest() {
    override val method: String = "Input.SendText"

    override fun getParams(): Map<String, Any?> = mapOf("text" to text, "done" to done)
}
