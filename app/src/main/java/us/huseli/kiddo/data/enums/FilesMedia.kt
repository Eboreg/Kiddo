package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class FilesMedia {
    @SerializedName("files") Files,
    @SerializedName("music") Music,
    @SerializedName("pictures") Pictures,
    @SerializedName("programs") Programs,
    @SerializedName("video") Video,
}
