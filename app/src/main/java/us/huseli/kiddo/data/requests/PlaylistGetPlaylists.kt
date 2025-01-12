package us.huseli.kiddo.data.requests

import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.AbstractRefRequest
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import java.lang.reflect.Type

class PlaylistGetPlaylists() : AbstractRefRequest<List<PlaylistGetPlaylists.ResultItem>>() {
    override val method: String = "Playlist.GetPlaylists"
    override val typeOfResult: Type =
        TypeToken.getParameterized(ArrayList::class.java, ResultItem::class.java).type

    override fun getParams(): Map<String, Any?> = emptyMap()

    data class ResultItem(
        override val playlistid: Int,
        val type: PlaylistType,
    ) : IHasPlaylistId
}
