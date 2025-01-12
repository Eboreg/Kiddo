package us.huseli.kiddo.data.types

import us.huseli.kiddo.KodiError

data class KodiJsonRpcResponseError(
    val code: Int,
    val data: Data?,
    override val message: String,
) : KodiError(message) {
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
