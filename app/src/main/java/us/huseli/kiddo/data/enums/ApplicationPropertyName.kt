package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.requests.interfaces.IRequestProperty

@Suppress("unused")
enum class ApplicationPropertyName : IRequestProperty {
    @SerializedName("language") Language,
    @SerializedName("muted") Muted,
    @SerializedName("name") Name,
    @SerializedName("sorttokens") SortTokens,
    @SerializedName("version") Version,
    @SerializedName("volume") Volume,
}
