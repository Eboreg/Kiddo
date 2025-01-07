package us.huseli.kiddo.data.types

data class KodiJsonRpcResponseError(
    val code: Int,
    val data: Data?,
    val message: String,
) {
    data class Data(
        val method: String,
        val stack: Stack,
        val message: String,
    ) {
        data class Stack(
            val name: String,
            val type: String,
        )
    }
}
