package us.huseli.kiddo.data.requests.interfaces

import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.KodiJsonRpcResponse

interface IRequestBoolResult : IRequest<Boolean> {
    override val responseTypeToken: TypeToken<*>
        get() = object : TypeToken<KodiJsonRpcResponse<Boolean>>() {}
}