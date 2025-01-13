package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.FilesMedia
import us.huseli.kiddo.data.types.ListItemSource
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import java.lang.reflect.Type

class FilesGetSources(
    val media: FilesMedia,
    val limits: ListLimits? = null,
    val sort: ListSort? = null,
) : AbstractRefRequest<FilesGetSources.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "Files.GetSources"

    override fun getParams(): Any {
        return mapOf("media" to media, "limits" to limits, "sort" to sort)
    }

    data class Result(
        val limits: ListLimitsReturned,
        val sources: List<ListItemSource>,
    )
}
