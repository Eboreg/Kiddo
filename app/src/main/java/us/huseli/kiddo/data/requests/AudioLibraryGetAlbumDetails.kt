package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.AudioFieldsAlbum
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import java.lang.reflect.Type

class AudioLibraryGetAlbumDetails(
    val albumId: Int,
    val properties: List<AudioFieldsAlbum> = emptyList(),
) : AbstractRefRequest<AudioLibraryGetAlbumDetails.Result>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "AudioLibrary.GetAlbumDetails"

    override fun getParams(): Map<String, Any?> = mapOf("albumid" to albumId, "properties" to properties)

    data class Result(val albumdetails: AudioDetailsAlbum?)
}
