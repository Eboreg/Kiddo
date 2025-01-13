package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.AbstractRefRequest
import java.lang.reflect.Type

class FilesPrepareDownload(val path: String) : AbstractRefRequest<FilesPrepareDownload.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "Files.PrepareDownload"

    override fun getParams(): Any {
        return mapOf("path" to path)
    }

    data class Result(
        val details: Details,
        val mode: Mode,
        val protocol: Protocol,
    ) {
        data class Details(
            val path: String?,
        )

        @Suppress("unused")
        enum class Mode {
            @SerializedName("redirect") Redirect,
            @SerializedName("direct") Direct,
        }

        @Suppress("unused")
        enum class Protocol {
            @SerializedName("http") Http,
        }
    }
}
