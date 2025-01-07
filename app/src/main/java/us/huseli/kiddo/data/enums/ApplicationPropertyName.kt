package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class ApplicationPropertyName {
    @SerializedName("language") Language,
    @SerializedName("muted") Muted,
    @SerializedName("name") Name,
    @SerializedName("sorttokens") SortTokens,
    @SerializedName("version") Version,
    @SerializedName("volume") Volume,
}
