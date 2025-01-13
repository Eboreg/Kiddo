package us.huseli.kiddo.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.KodiError
import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.retaintheme.request.Request
import us.huseli.retaintheme.utils.ILogger
import java.lang.reflect.Type

abstract class AbstractRequest<Result : Any> : ILogger {
    enum class Status { Pending, Failed, Succeeded }

    private var _result: Result? = null
    private var _requestId: Int? = null

    abstract val method: String
    abstract val responseTypeToken: TypeToken<KodiJsonRpcResponse<Result>>

    val requestId: Int
        get() = _requestId!!
    val result: Result
        get() = _result!!
    val resultOrNull: Result?
        get() = _result
    var status: Status = Status.Pending
        private set

    abstract fun getParams(): Any

    suspend fun post(
        url: String,
        requestId: Int,
        headers: Map<String, String>
    ): List<KodiJsonRpcResponse<Result>> {
        val responseListType = TypeToken.getParameterized(ArrayList::class.java, responseTypeToken.type).type
        val params = getParams()
        val json = listOf(
            mapOf(
                "jsonrpc" to "2.0",
                "method" to method,
                "params" to params,
                "id" to requestId,
            )
        )
        val responseJson = Request.postJson(url = url, json = json, gson = gson, headers = headers).getString()

        if (responseJson.length <= 900) log("responseJson=$responseJson", priority = Log.DEBUG)

        val response = gson.fromJson<List<KodiJsonRpcResponse<Result>>>(responseJson, responseListType)
            .map { it.copy(method = method, request = this) }
            .also { response ->
                log("method=$method, requestId=$requestId, params=$params")
                log("response=$response", priority = if (response.isError) Log.ERROR else Log.DEBUG)
            }
        val result = response.result
        val error = response.error

        _requestId = requestId

        if (error != null) {
            status = Status.Failed
            throw error
        }
        if (result != null) {
            _result = result
            status = Status.Succeeded
        } else {
            status = Status.Failed
            throw KodiError("$method request returned null")
        }

        return response
    }

    companion object {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(GlobalToggle::class.java, GlobalToggle.TypeAdapter())
            .create()
    }
}

abstract class AbstractRefRequest<Result : Any> : AbstractRequest<Result>() {
    abstract val typeOfResult: Type

    @Suppress("UNCHECKED_CAST")
    override val responseTypeToken: TypeToken<KodiJsonRpcResponse<Result>>
        get() = TypeToken.getParameterized(
            KodiJsonRpcResponse::class.java,
            typeOfResult,
        ) as TypeToken<KodiJsonRpcResponse<Result>>
}

abstract class AbstractBoolRequest : AbstractRequest<Boolean>() {
    override val responseTypeToken: TypeToken<KodiJsonRpcResponse<Boolean>>
        get() = object : TypeToken<KodiJsonRpcResponse<Boolean>>() {}
}

abstract class AbstractIntRequest : AbstractRequest<Int>() {
    override val responseTypeToken: TypeToken<KodiJsonRpcResponse<Int>>
        get() = object : TypeToken<KodiJsonRpcResponse<Int>>() {}
}

abstract class AbstractStringRequest : AbstractRequest<String>() {
    override val responseTypeToken: TypeToken<KodiJsonRpcResponse<String>>
        get() = object : TypeToken<KodiJsonRpcResponse<String>>() {}
}
