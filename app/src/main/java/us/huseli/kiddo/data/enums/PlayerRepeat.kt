package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class PlayerRepeat {
    @SerializedName("all") All,
    @SerializedName("off") Off,
    @SerializedName("one") One,
}
