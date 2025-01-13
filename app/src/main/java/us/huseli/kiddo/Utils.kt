package us.huseli.kiddo

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import us.huseli.retaintheme.extensions.filterValuesNotNull
import us.huseli.retaintheme.extensions.seconds
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

fun <K, V> mapOfNotNull(vararg pairs: Pair<K, V?>): Map<K, V> = mapOf(*pairs).filterValuesNotNull()

@Composable
fun Int.videoRuntime(): String? = takeIf { it > 0 }?.seconds?.inWholeMinutes?.let { stringResource(R.string.x_min, it) }
