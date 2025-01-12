package us.huseli.kiddo.data

import us.huseli.kiddo.data.types.KodiJsonRpcResponseError

data class KodiJsonRpcResponse<Result : Any>(
    val id: Int,
    val jsonrpc: String,
    val method: String = "",
    val error: KodiJsonRpcResponseError? = null,
    val result: Result? = null,
    val request: AbstractRequest<Result>? = null,
) {
    val isError: Boolean
        get() = error != null
}

val Iterable<KodiJsonRpcResponse<*>>.isError: Boolean
    get() = any { it.isError }

val Iterable<KodiJsonRpcResponse<*>>.error: KodiJsonRpcResponseError?
    get() = firstNotNullOfOrNull { it.error }

val <Result : Any> Iterable<KodiJsonRpcResponse<Result>>.result: Result?
    get() = firstNotNullOfOrNull { it.result }
