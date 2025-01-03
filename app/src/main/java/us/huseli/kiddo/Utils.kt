package us.huseli.kiddo

import kotlin.time.Duration

fun Duration.toTimeMap() = toComponents { hours, minutes, seconds, nanoseconds ->
    mapOf(
        "hours" to hours.toInt(),
        "milliseconds" to nanoseconds * 1000,
        "minutes" to minutes,
        "seconds" to seconds,
    )
}
