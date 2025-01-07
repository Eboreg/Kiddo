package us.huseli.kiddo.data.requests.interfaces

import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.KodiJsonRpcResponse

interface IRequestStringResult : IRequest<String> {
    override val responseTypeToken: TypeToken<*>
        get() = object : TypeToken<KodiJsonRpcResponse<String>>() {}
}