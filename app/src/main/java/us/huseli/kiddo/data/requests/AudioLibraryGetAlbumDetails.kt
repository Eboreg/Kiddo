package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.AbstractItemRequest
import us.huseli.kiddo.data.enums.AudioFieldsAlbum
import us.huseli.kiddo.data.requests.interfaces.IItemResult
import us.huseli.kiddo.data.types.AudioDetailsAlbum
import java.lang.reflect.Type

class AudioLibraryGetAlbumDetails(
    val albumId: Int,
    override val properties: List<AudioFieldsAlbum> = listOf(
        AudioFieldsAlbum.AlbumDuration,
        AudioFieldsAlbum.AlbumStatus,
        AudioFieldsAlbum.Art,
        AudioFieldsAlbum.Artist,
        AudioFieldsAlbum.ArtistId,
        AudioFieldsAlbum.Compilation,
        AudioFieldsAlbum.Description,
        AudioFieldsAlbum.DisplayArtist,
        AudioFieldsAlbum.FanArt,
        AudioFieldsAlbum.Genre,
        AudioFieldsAlbum.Mood,
        AudioFieldsAlbum.PlayCount,
        AudioFieldsAlbum.Rating,
        AudioFieldsAlbum.SongGenres,
        AudioFieldsAlbum.Style,
        AudioFieldsAlbum.Theme,
        AudioFieldsAlbum.Thumbnail,
        AudioFieldsAlbum.Title,
        AudioFieldsAlbum.TotalDiscs,
        AudioFieldsAlbum.Type,
        AudioFieldsAlbum.Votes,
        AudioFieldsAlbum.Year,
    ),
) : AbstractItemRequest<AudioDetailsAlbum>() {
    override val typeOfResult: Type = Result::class.java
    override val method: String = "AudioLibrary.GetAlbumDetails"

    override fun getParams(): Map<String, Any?> = mapOf("albumid" to albumId, "properties" to properties)

    @Immutable
    data class Result(val albumdetails: AudioDetailsAlbum?) : IItemResult<AudioDetailsAlbum> {
        override val item: AudioDetailsAlbum?
            get() = albumdetails
    }
}
