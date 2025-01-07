package us.huseli.kiddo.data.requests.interfaces

import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.KodiJsonRpcResponse

interface IRequestIntResult : IRequest<Int> {
    override val responseTypeToken: TypeToken<*>
        get() = object : TypeToken<KodiJsonRpcResponse<Int>>() {}
}