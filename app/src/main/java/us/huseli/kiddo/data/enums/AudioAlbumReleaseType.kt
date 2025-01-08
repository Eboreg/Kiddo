package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class AudioAlbumReleaseType {
    @SerializedName("album") Album,
    @SerializedName("single") Single,
}
