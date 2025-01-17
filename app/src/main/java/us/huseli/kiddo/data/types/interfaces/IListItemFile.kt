package us.huseli.kiddo.data.types.interfaces

import com.google.gson.annotations.SerializedName

interface IListItemFile : IListItemBase {
    override val file: String
    val filetype: FileType
    val lastmodified: String?
    val mimetype: String?
    val size: Long?

    @Suppress("unused")
    enum class FileType {
        @SerializedName("file") File,
        @SerializedName("directory") Directory,
    }
}
