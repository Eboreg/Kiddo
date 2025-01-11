package us.huseli.kiddo

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import us.huseli.retaintheme.ui.theme.LocalBasicColors
import us.huseli.retaintheme.utils.ILogger
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Suppress("unused")
fun Duration.toTimeMap() = toComponents { hours, minutes, seconds, nanoseconds ->
    mapOf(
        "hours" to hours.toInt(),
        "milliseconds" to nanoseconds * 1000,
        "minutes" to minutes,
        "seconds" to seconds,
    )
}

fun String.takeIfNotEmpty() = takeIf { it.isNotEmpty() }

fun String.takeIfNotBlank() = takeIf { it.isNotBlank() }

fun <T> Set<T>.takeIfNotEmpty() = takeIf { it.isNotEmpty() }

fun <T> List<T>.takeIfNotEmpty() = takeIf { it.isNotEmpty() }

fun <T> Collection<T>.takeIfNotEmpty() = takeIf { it.isNotEmpty() }

fun String.stripTags() = replace(Regex("\\[/?B]"), "")

object Logger : ILogger

@Composable
fun randomBasicColor(): Color {
    val colors = LocalBasicColors.current
    val colorList = remember {
        listOf(
            colors.Blue,
            colors.Brown,
            colors.Cerulean,
            colors.Gray,
            colors.Green,
            colors.Orange,
            colors.Pink,
            colors.Purple,
            colors.Red,
            colors.Teal,
            colors.Yellow,
        )
    }

    return colorList.random()
}

@Suppress("UNCHECKED_CAST")
fun <K, V> Map<K, V?>.filterValuesNotNull(): Map<K, V> = filterValues { it != null } as Map<K, V>

fun <K, V> Map<K, V>.takeIfNotEmpty(): Map<K, V>? = takeIf { it.isNotEmpty() }

fun Duration.sensibleFormat(withSeconds: Boolean = true) =
    toComponents { hours, minutes, seconds, _ ->
        if (withSeconds && hours > 0) String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        else if (withSeconds) String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        else String.format(Locale.getDefault(), "%d:%02d", hours, minutes)
    }


fun Modifier.clickableIfNotNull(onClick: (() -> Unit)?) = onClick?.let { clickable(onClick = it) } ?: this

fun <T> Modifier.clickableIfNotNull(onClick: ((T) -> Unit)?, arg: T) =
    onClick?.let { clickable { onClick(arg) } } ?: this

fun Collection<String>.cleanDuplicates(ignoreCase: Boolean = true): Collection<String> =
    associateBy { if (ignoreCase) it.lowercase() else it }.values

val Int.seconds: Duration
    get() = toDuration(DurationUnit.SECONDS)

val Int.hours: Duration
    get() = toDuration(DurationUnit.HOURS)

val Int.milliseconds: Duration
    get() = toDuration(DurationUnit.MILLISECONDS)

val Int.minutes: Duration
    get() = toDuration(DurationUnit.MINUTES)
