package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.ListFieldsFiles
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.types.ListItemFile
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import java.lang.reflect.Type

class FilesGetDirectory(
    val directory: String,
    override val properties: List<ListFieldsFiles> = emptyList(),
    override val sort: ListSort? = null,
    override val limits: ListLimits? = null,
) : AbstractListRequest<ListItemFile>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "Files.GetDirectory"

    override fun getParams(): Map<String, Any?> {
        return mapOf(
            "directory" to directory,
            "properties" to properties,
            "sort" to sort,
            "limits" to limits,
        )
    }

    data class Result(
        val files: List<ListItemFile>,
        override val limits: ListLimitsReturned,
    ) : IListResult<ListItemFile> {
        override val items: List<ListItemFile>
            get() = files
    }
}
