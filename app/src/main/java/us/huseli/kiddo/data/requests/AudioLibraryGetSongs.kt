package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.AudioFieldsSong
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.data.types.ListFilterSongs
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.retaintheme.extensions.filterValuesNotNull
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import java.lang.reflect.Type

class AudioLibraryGetSongs(
    val properties: List<AudioFieldsSong>? = null,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
    val filter: ListFilterSongs? = null,
    val simpleFilter: SimpleFilter? = null,
    val includeSingles: Boolean? = null,
    val singlesOnly: Boolean? = null,
) : AbstractRefRequest<AudioLibraryGetSongs.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "AudioLibrary.GetSongs"

    override fun getParams(): Map<String, Any?> {
        val filterMap = listOfNotNull(filter?.getParams(), simpleFilter?.getParams())
            .takeIfNotEmpty()
            ?.reduce { acc, m -> acc + m }
            ?.takeIfNotEmpty()

        return mapOf(
            "properties" to properties,
            "limits" to limits,
            "sort" to sort,
            "filter" to filterMap,
            "includesingles" to includeSingles,
            "singlesonly" to singlesOnly,
        )
    }

    data class SimpleFilter(
        val genreId: Int? = null,
        val genre: String? = null,
        val artistId: Int? = null,
        val artist: Artist? = null,
        val albumId: Int? = null,
        val album: String? = null,
    ) {
        data class Artist(val artist: String, val roleId: Int? = null, val role: String? = null)

        fun getParams(): Map<String, Any> {
            return mapOf(
                "genreid" to genreId,
                "genre" to genre,
                "artistid" to artistId,
                "artist" to artist?.let { mapOf("artist" to it.artist, "roleid" to it.roleId, "role" to it.role) },
                "albumid" to albumId,
                "album" to album,
            ).filterValuesNotNull()
        }
    }

    data class Result(
        val limits: ListLimitsReturned,
        val songs: List<AudioDetailsSong>,
    )
}
