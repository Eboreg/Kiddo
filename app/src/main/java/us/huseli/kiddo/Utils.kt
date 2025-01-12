package us.huseli.kiddo

import us.huseli.retaintheme.utils.ILogger
import java.util.Locale
import kotlin.time.Duration

fun String.stripTags() = replace(Regex("\\[/?B]"), "")

@Suppress("unused") object Logger : ILogger

fun Duration.sensibleFormat(withSeconds: Boolean = true) =
    toComponents { hours, minutes, seconds, _ ->
        if (withSeconds && hours > 0) String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        else if (withSeconds) String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        else String.format(Locale.getDefault(), "%d:%02d", hours, minutes)
    }
