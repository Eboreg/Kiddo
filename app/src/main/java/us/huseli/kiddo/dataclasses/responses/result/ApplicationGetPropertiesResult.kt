package us.huseli.kiddo.dataclasses.responses.result

import com.google.gson.annotations.SerializedName

data class ApplicationGetPropertiesResult(
    val language: String?,
    val muted: Boolean?,
    val name: String?,
    val sorttokens: List<String>?,
    val version: Version?,
    val volume: Int?,
) {
    data class Version(
        val major: Int,
        val minor: Int,
        val revision: Any?,
        val tag: Tag,
        val tagversion: String?,
    ) {
        @Suppress("unused")
        enum class Tag {
            @SerializedName("prealpha") PreAlpha,
            @SerializedName("alpha") Alpha,
            @SerializedName("beta") Beta,
            @SerializedName("releasecandidate") ReleaseCandidate,
            @SerializedName("stable") Stable,
        }
    }
}
