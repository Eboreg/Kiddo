package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.FilesMedia

data class PlaylistItem(
    val file: String? = null,
    val directory: Directory? = null,
    val movieId: Int? = null,
    val episodeId: Int? = null,
    val musicVideoId: Int? = null,
    val artistId: Int? = null,
    val albumId: Int? = null,
    val songId: Int? = null,
    val genreId: Int? = null,
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
