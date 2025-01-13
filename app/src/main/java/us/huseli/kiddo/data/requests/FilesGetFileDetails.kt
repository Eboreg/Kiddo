package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ListFieldsFiles
import us.huseli.kiddo.data.types.ListItemFile
import java.lang.reflect.Type

class FilesGetFileDetails(
    val file: String,
    val properties: List<ListFieldsFiles>? = null,
) : AbstractRefRequest<FilesGetFileDetails.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "Files.GetFileDetails"

    override fun getParams(): Any {
        return mapOf("file" to file, "properties" to properties)
    }

    data class Result(val filedetails: ListItemFile)
}
