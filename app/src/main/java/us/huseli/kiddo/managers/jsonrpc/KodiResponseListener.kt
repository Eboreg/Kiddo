package us.huseli.kiddo.managers.jsonrpc

import us.huseli.kiddo.KodiError
import us.huseli.kiddo.data.AbstractRequest

interface KodiResponseListener {
    fun <Result : Any> onKodiRequestSucceeded(request: AbstractRequest<Result>, result: Result)
    fun onKodiRequestError(error: KodiError) {}
}
