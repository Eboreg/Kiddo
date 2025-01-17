package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.AudioFieldsSong
import us.huseli.kiddo.data.enums.ListFilterFieldsSongs
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.retaintheme.extensions.filterValuesNotNull
import java.lang.reflect.Type

class AudioLibraryGetSongs(
    override val properties: List<AudioFieldsSong> = listOf(
        AudioFieldsSong.Artist,
        AudioFieldsSong.Bitrate,
        AudioFieldsSong.Disc,
        AudioFieldsSong.DisplayArtist,
        AudioFieldsSong.Duration,
        AudioFieldsSong.File,
        AudioFieldsSong.Genre,
        AudioFieldsSong.Mood,
        AudioFieldsSong.PlayCount,
        AudioFieldsSong.Title,
        AudioFieldsSong.Track,
        AudioFieldsSong.Year,
    ),
    override val limits: ListLimits? = null,
    override val sort: ListSort? = null,
    override val filter: ListFilter<ListFilterFieldsSongs>? = null,
    override val simpleFilter: SimpleFilter? = null,
    val includeSingles: Boolean? = null,
    val singlesOnly: Boolean? = null,
    val allRoles: Boolean? = null,
) : AbstractListRequest<AudioDetailsSong>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "AudioLibrary.GetSongs"

    override fun getParams(): Map<String, Any?> {
        return super.getParams() + mapOf(
            "includesingles" to includeSingles,
            "singlesonly" to singlesOnly,
            "allroles" to allRoles,
        )
    }

    data class SimpleFilter(
        val genreId: Int? = null,
        val genre: String? = null,
        val artistId: Int? = null,
        val artist: Artist? = null,
        val albumId: Int? = null,
        val album: String? = null,
    ) : ISimpleFilter {
        data class Artist(val artist: String, val roleId: Int? = null, val role: String? = null)

        override fun getParams(): Map<String, Any> {
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
        override val limits: ListLimitsReturned,
        val songs: List<AudioDetailsSong>?,
    ) : IListResult<AudioDetailsSong> {
        override val items: List<AudioDetailsSong>?
            get() = songs
    }
}
