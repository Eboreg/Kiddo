package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractStringRequest

class InputKeyPress(key: String) : AbstractStringRequest() {
    override val method: String = "Input.$key"

    override fun getParams(): Map<String, Any?> = emptyMap()
}
