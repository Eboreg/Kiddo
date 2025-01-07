package us.huseli.kiddo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import us.huseli.retaintheme.ui.theme.LocalBasicColors
import us.huseli.retaintheme.utils.ILogger
import kotlin.time.Duration

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

fun <T> List<T>.takeIfNotEmpty() = takeIf { it.isNotEmpty() }

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
