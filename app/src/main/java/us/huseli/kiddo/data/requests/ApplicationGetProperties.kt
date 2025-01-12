package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.ApplicationPropertyName
import java.lang.reflect.Type

class ApplicationGetProperties(val properties: List<ApplicationPropertyName>) :
    AbstractRefRequest<ApplicationGetProperties.Result>() {
    override val method: String = "Application.GetProperties"
    override val typeOfResult: Type = Result::class.java

    override fun getParams(): Map<String, Any?> = mapOf("properties" to properties)

    data class Result(
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
            val revision: Any?, // May be either string or integer
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
}
