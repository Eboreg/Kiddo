package us.huseli.kiddo.data.requests.interfaces

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.KodiJsonRpcResponse
import us.huseli.kiddo.data.enums.GlobalToggle
import us.huseli.retaintheme.request.Request
import us.huseli.retaintheme.utils.ILogger

interface IRequest<Result> : ILogger {
    val method: String
    val responseTypeToken: TypeToken<*>

    fun getParams(): Map<String, Any?>

    suspend fun post(
        url: String,
        requestId: Int,
        headers: Map<String, String> = emptyMap(),
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

        return try {
            gson.fromJson<List<KodiJsonRpcResponse<Result>>>(responseJson, responseListType)
                .map { it.copy(method = method, request = this) }
                .also { response ->
                    log("post: method=$method, params=$params")
                    if (response.any { it.error != null } == true) logError(message = "post: response=$response")
                    else log("post: response=$response")
                }
        } catch (e: Throwable) {
            logError("post: responseJson=$responseJson", e)
            throw e
        }
    }

    companion object {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(GlobalToggle::class.java, GlobalToggle.TypeAdapter())
            .create()
    }
}
