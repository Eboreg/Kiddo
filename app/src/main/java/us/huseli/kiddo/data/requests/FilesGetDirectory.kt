package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ListFieldsFiles
import us.huseli.kiddo.data.types.ListItemFile
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import java.lang.reflect.Type

class FilesGetDirectory(
    val directory: String,
    val properties: List<ListFieldsFiles>? = null,
    val sort: ListSort? = null,
    val limits: ListLimits? = null,
) : AbstractRefRequest<FilesGetDirectory.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "Files.GetDirectory"

    override fun getParams(): Any {
        return mapOf(
            "directory" to directory,
            "properties" to properties,
            "sort" to sort,
            "limits" to limits,
        )
    }

    data class Result(
        val files: List<ListItemFile>,
        val limits: ListLimitsReturned,
    )
}
