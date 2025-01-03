package us.huseli.kiddo.dataclasses.responses

data class KodiJsonRpcResponse<Result>(
    val id: Int,
    val jsonrpc: String,
    val error: Error? = null,
    val result: Result? = null,
) {
    data class Error(
        val code: Int,
        val data: Data,
        val message: String,
    ) {
        data class Data(
            val method: String,
            val stack: Stack,
        ) {
            data class Stack(
                val message: String,
                val name: String,
                val type: List<String>,
            )
        }
    }
}
