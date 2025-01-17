package us.huseli.kiddo

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import us.huseli.retaintheme.extensions.filterValuesNotNull
import us.huseli.retaintheme.extensions.seconds
import us.huseli.retaintheme.utils.ILogger
import java.util.Locale
import kotlin.math.pow
import kotlin.time.Duration

val KILOBYTE = 2.0.pow(10)
val MEGABYTE = 2.0.pow(20)
val GIGABYTE = 2.0.pow(30)
val TERABYTE = 2.0.pow(40)

fun String.stripTags() = replace(Regex("\\[/?B]"), "")

object Logger : ILogger

fun Duration.sensibleFormat(withSeconds: Boolean = true) =
    toComponents { hours, minutes, seconds, _ ->
        if (withSeconds && hours > 0) String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds)
        else if (withSeconds) String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
        else String.format(Locale.getDefault(), "%d:%02d", hours, minutes)
    }

fun <K, V> mapOfNotNull(vararg pairs: Pair<K, V?>): Map<K, V> = mapOf(*pairs).filterValuesNotNull()

@Composable
fun Int.videoRuntime(): String? = takeIf { it > 0 }?.seconds?.inWholeMinutes?.let { stringResource(R.string.x_min, it) }

fun Long.formatFileSize(precision: Int = 1): String {
    val locale = Locale.getDefault()

    return when {
        this > TERABYTE -> String.format(locale, "%.${precision}f TB", this / TERABYTE)
        this > GIGABYTE -> String.format(locale, "%.${precision}f GB", this / GIGABYTE)
        this > MEGABYTE -> String.format(locale, "%.${precision}f MB", this / MEGABYTE)
        this > KILOBYTE -> String.format(locale, "%.${precision}f KB", this / KILOBYTE)
        else -> String.format(locale, "%d B", this)
    }
}

fun Uri.getDisplayName(context: Context) = context.contentResolver.query(this, null, null, null, null)?.use { c ->
    if (c.moveToFirst()) c.getColumnIndex(OpenableColumns.DISPLAY_NAME).takeIf { it >= 0 }?.let { c.getString(it) }
    else null
}
