package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.enums.FilesMedia

@Immutable
data class PlaylistItem(
    val albumId: Int? = null,
    val artistId: Int? = null,
    val directory: Directory? = null,
    val episodeId: Int? = null,
    val file: String? = null,
    val genreId: Int? = null,
    val movieId: Int? = null,
    val musicVideoId: Int? = null,
    val songId: Int? = null,
) {
    fun getParams(): Map<String, Any?> {
        return mapOf(
            "file" to file,
            "movieid" to movieId,
            "episodeid" to episodeId,
            "musicvideoid" to musicVideoId,
            "artistid" to artistId,
            "albumid" to albumId,
            "songid" to songId,
            "genreid" to genreId,
        ).let { if (directory != null) it + directory.getParams() else it }
    }

    @Immutable
    data class Directory(
        val directory: String,
        val media: FilesMedia? = null,
        val recursive: Boolean? = null,
    ) {
        fun getParams(): Map<String, Any?> = mapOf(
            "directory" to directory,
            "media" to media,
            "recursive" to recursive,
        )
    }
}
