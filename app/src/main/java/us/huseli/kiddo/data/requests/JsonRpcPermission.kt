package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.AbstractRefRequest
import java.lang.reflect.Type

class JsonRpcPermission : AbstractRefRequest<JsonRpcPermission.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "JSONRPC.Permission"

    override fun getParams(): Map<String, Any?> = emptyMap()

    @Immutable
    data class Result(
        val controlgui: Boolean,
        val controlnotify: Boolean,
        val controlplayback: Boolean,
        val controlpower: Boolean,
        val controlsystem: Boolean,
        val executeaddon: Boolean,
        val manageaddon: Boolean,
        val navigate: Boolean,
        val readdata: Boolean,
        val removedata: Boolean,
        val updatedate: Boolean,
        val writefile: Boolean,
    )
}
