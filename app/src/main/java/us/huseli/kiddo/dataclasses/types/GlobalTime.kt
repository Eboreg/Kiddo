package us.huseli.kiddo.dataclasses.types

import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class GlobalTime(
    val hours: Int,
    val milliseconds: Int,
    val minutes: Int,
    val seconds: Int,
) {
    val duration: Duration
        get() = hours.toDuration(DurationUnit.HOURS) +
            milliseconds.toDuration(DurationUnit.MILLISECONDS) +
            minutes.toDuration(DurationUnit.MINUTES) +
            seconds.toDuration(DurationUnit.SECONDS)
}
