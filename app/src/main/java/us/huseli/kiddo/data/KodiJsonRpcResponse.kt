package us.huseli.kiddo.data

import us.huseli.kiddo.data.requests.interfaces.IRequest
import us.huseli.kiddo.data.types.KodiJsonRpcResponseError

data class KodiJsonRpcResponse<Result>(
    val id: Int,
    val jsonrpc: String,
    val method: String = "",
    val error: KodiJsonRpcResponseError? = null,
    val result: Result? = null,
    val request: IRequest<Result>? = null,
) {
    val isError: Boolean
        get() = error != null
}
