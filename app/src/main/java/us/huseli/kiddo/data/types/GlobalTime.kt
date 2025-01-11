package us.huseli.kiddo.data.types

import us.huseli.kiddo.hours
import us.huseli.kiddo.milliseconds
import us.huseli.kiddo.minutes
import us.huseli.kiddo.seconds
import kotlin.time.Duration
import kotlin.time.DurationUnit

data class GlobalTime(
    val hours: Int = 0,
    val milliseconds: Int = 0,
    val minutes: Int = 0,
    val seconds: Int = 0,
) {
    val duration: Duration
        get() = hours.hours + milliseconds.milliseconds + minutes.minutes + seconds.seconds

    val totalMilliseconds: Long
        get() = duration.toLong(DurationUnit.MILLISECONDS)
}
