package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class VideoResume(
    val position: Double?,
    val total: Double?,
) {
    val progress: Float
        get() = if (position != null && total != null && total > 0.0) (position / total).toFloat() else 0f
}
