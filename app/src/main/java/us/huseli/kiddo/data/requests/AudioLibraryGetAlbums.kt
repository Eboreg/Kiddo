package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.AudioFieldsAlbum
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.requests.interfaces.ISimpleFilter
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.retaintheme.extensions.filterValuesNotNull
import java.lang.reflect.Type

class AudioLibraryGetAlbums(
    override val properties: List<AudioFieldsAlbum> = listOf(
        AudioFieldsAlbum.AlbumDuration,
        AudioFieldsAlbum.Art,
        AudioFieldsAlbum.Artist,
        AudioFieldsAlbum.Genre,
        AudioFieldsAlbum.Rating,
        AudioFieldsAlbum.Title,
        AudioFieldsAlbum.Year,
    ),
    override val limits: ListLimits? = null,
    override val sort: ListSort? = null,
    override val filter: ListFilter<ListFilterFieldsAlbums>? = null,
    override val simpleFilter: SimpleFilter? = null,
    val includeSingles: Boolean? = null,
    val allRoles: Boolean? = null,
) : AbstractListRequest<AudioDetailsAlbum>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "AudioLibrary.GetAlbums"

    override fun getParams(): Map<String, Any?> {
        return super.getParams() + mapOf(
            "includesingles" to includeSingles,
            "allroles" to allRoles,
        )
    }

    data class SimpleFilter(
        val genreId: Int? = null,
        val genre: String? = null,
        val artistId: Int? = null,
        val artistIdWithRoleId: ArtistIdWithRoleId? = null,
        val artistIdWithRole: ArtistIdWithRole? = null,
        val artist: String? = null,
        val artistWithRoleId: ArtistWithRoleId? = null,
        val artistWithRole: ArtistWithRole? = null,
    ) : ISimpleFilter {
        override fun getParams(): Map<String, Any> {
            return mutableMapOf(
                "genreid" to genreId,
                "genre" to genre,
                "artistid" to artistId,
                "artist" to artist,
            ).apply {
                artistIdWithRoleId?.also {
                    set("artistid", it.artistId)
                    set("roleid", it.roleId)
                }
                artistIdWithRole?.also {
                    set("artistid", it.artistId)
                    set("role", it.role)
                }
                artistWithRoleId?.also {
                    set("artist", it.artist)
                    set("roleid", it.roleId)
                }
                artistWithRole?.also {
                    set("artist", it.artist)
                    set("role", it.role)
                }
            }.filterValuesNotNull()
        }

        data class ArtistIdWithRoleId(val artistId: Int, val roleId: Int)
        data class ArtistIdWithRole(val artistId: Int, val role: String)
        data class ArtistWithRoleId(val artist: String, val roleId: Int)
        data class ArtistWithRole(val artist: String, val role: String)
    }

    data class Result(
        override val limits: ListLimitsReturned,
        val albums: List<AudioDetailsAlbum>?,
    ) : IListResult<AudioDetailsAlbum> {
        override val items: List<AudioDetailsAlbum>?
            get() = albums
    }
}
