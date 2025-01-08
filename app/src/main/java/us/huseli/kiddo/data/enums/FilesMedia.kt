package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class FilesMedia {
    @SerializedName("video") Video,
    @SerializedName("music") Music,
    @SerializedName("pictures") Pictures,
    @SerializedName("files") Files,
    @SerializedName("programs") Programs,
}
