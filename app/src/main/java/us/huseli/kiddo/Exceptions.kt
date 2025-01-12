package us.huseli.kiddo

import java.net.SocketTimeoutException

open class KodiError(message: String?, cause: Throwable? = null) : Exception(message, cause) {
    open val defaultMessage: String = "Unknown error"

    override fun toString(): String = message ?: defaultMessage
}

@Suppress("CanBeParameter")
open class KodiConnectionError(
    val url: String,
    cause: Throwable? = null,
    message: String? = null,
) : KodiError(message, cause) {
    override val defaultMessage: String = "Error connecting to $url" + (cause?.message?.let { ": $it" } ?: "")
}

class KodiConnectionTimeoutError(url: String, override val cause: SocketTimeoutException, message: String? = null) :
    KodiConnectionError(url, cause, message) {
    override val defaultMessage: String = "Connection timeout from $url: $cause"
}

class KodiWebsocketReceiveError(url: String, override val cause: Throwable, message: String? = null) :
    KodiConnectionError(url, cause, message) {
    override val defaultMessage: String = "Error receiving from $url" + (cause.message?.let { ": $it" } ?: "")
}
