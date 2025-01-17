package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class VideoStreams(
    val audio: List<Audio>?,
    val subtitle: List<Subtitle>?,
    val video: List<Video>?,
) {
    @Immutable
    data class Audio(
        val channels: Int?,
        val codec: String?,
        val language: String?,
    )

    @Immutable
    data class Subtitle(
        val language: String?,
    )

    @Immutable
    data class Video(
        val aspect: Double?,
        val codec: String?,
        val duration: Int?,
        val hdrtype: String?,
        val height: Int?,
        val width: Int?,
    )
}
