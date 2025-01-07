package us.huseli.kiddo

open class KodiError(message: String, cause: Throwable? = null) : Exception(message, cause)

class KodiConnectionError(
    val url: String,
    cause: Throwable? = null,
    message: String? = cause?.message,
) : KodiError(message = "Error connecting to $url" + message?.let { ": $it" }, cause = cause)

class KodiWebsocketReceiveError(val url: String, cause: Throwable) :
    KodiError(message = "Error receiving from $url" + cause.message?.let { ": $it" }, cause = cause)
