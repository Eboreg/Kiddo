package us.huseli.kiddo.data.requests.interfaces

interface IRequestNoParams<Result> : IRequest<Result> {
    override fun getParams(): Map<String, Any?> {
        return emptyMap()
    }
}
