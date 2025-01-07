package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.requests.interfaces.IRequestNoParams
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult

class InputKeyPress(key: String) : IRequestStringResult, IRequestNoParams<String> {
    override val method: String = "Input.$key"
}
