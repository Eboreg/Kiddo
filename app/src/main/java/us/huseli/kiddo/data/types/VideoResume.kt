package us.huseli.kiddo.data.types

data class VideoResume(
    val position: Double?,
    val total: Double?,
) {
    val progress: Float
        get() = if (position != null && total != null && total > 0.0) (position / total).toFloat() else 0f
}
