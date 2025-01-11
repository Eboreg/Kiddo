package us.huseli.kiddo.data.requests.interfaces

import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.KodiJsonRpcResponse
import java.lang.reflect.Type

interface IRequestRefResult<Result> : IRequest<Result> {
    val typeOfResult: Type
    override val responseTypeToken: TypeToken<*>
        get() = TypeToken.getParameterized(KodiJsonRpcResponse::class.java, typeOfResult)
}
