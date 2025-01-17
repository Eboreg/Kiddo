package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.FilesMedia
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.types.ListItemSource
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import java.lang.reflect.Type

class FilesGetSources(
    val media: FilesMedia,
    override val limits: ListLimits? = null,
    override val sort: ListSort? = null,
) : AbstractListRequest<ListItemSource>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "Files.GetSources"

    override fun getParams(): Map<String, Any?> {
        return super.getParams() + ("media" to media)
    }

    data class Result(
        override val limits: ListLimitsReturned,
        val sources: List<ListItemSource>,
    ) : IListResult<ListItemSource> {
        override val items: List<ListItemSource>
            get() = sources
    }
}
