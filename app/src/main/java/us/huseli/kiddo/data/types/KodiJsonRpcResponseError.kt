package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.KodiError

@Immutable
data class KodiJsonRpcResponseError(
    val code: Int,
    val data: Data?,
    override val message: String,
) : KodiError(message) {
    @Immutable
    data class Data(
        val method: String,
        val stack: Stack,
        val message: String,
    ) {
        @Immutable
        data class Stack(
            val name: String,
            val type: String,
        )
    }
}
