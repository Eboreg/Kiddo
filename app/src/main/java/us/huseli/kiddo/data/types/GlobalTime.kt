package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@Immutable
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
