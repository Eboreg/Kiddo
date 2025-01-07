package us.huseli.kiddo.data.types.interfaces

interface IVideoDetailsFile : IVideoDetailsItem {
    val director: List<String>?
    val resume: VideoResume?
    val runtime: Int?
    val streamdetails: VideoStreams?

    data class VideoResume(
        val position: Double?,
        val total: Double?,
    )

    data class VideoStreams(
        val audio: List<Audio>?,
        val subtitle: List<Subtitle>?,
        val video: List<Video>?,
    ) {
        data class Audio(
            val channels: Int?,
            val codec: String?,
            val language: String?,
        )

        data class Subtitle(
            val language: String?,
        )

        data class Video(
            val aspect: Double?,
            val codec: String?,
            val duration: Int?,
            val hdrtype: String?,
            val height: Int?,
            val width: Int?,
        )
    }
}
