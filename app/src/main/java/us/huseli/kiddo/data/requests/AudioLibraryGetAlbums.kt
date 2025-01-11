package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.AudioFieldsAlbum
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import us.huseli.kiddo.data.types.ListFilterAlbums
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.takeIfNotEmpty
import java.lang.reflect.Type

class AudioLibraryGetAlbums(
    val properties: List<AudioFieldsAlbum>?,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
    val filter: ListFilterAlbums? = null,
    val includeSingles: Boolean? = null,
    val allRoles: Boolean? = null,
) : IRequestRefResult<AudioLibraryGetAlbums.Result> {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "AudioLibrary.GetAlbums"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "properties" to properties,
            "limits" to limits,
            "sort" to sort,
            "filter" to filter?.getParams()?.takeIfNotEmpty(),
            "includesingles" to includeSingles,
            "allroles" to allRoles,
        )
    }

    data class Result(
        val limits: ListLimitsReturned,
        val albums: List<AudioDetailsAlbum>,
    )
}
