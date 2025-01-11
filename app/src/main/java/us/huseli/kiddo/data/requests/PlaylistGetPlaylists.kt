package us.huseli.kiddo.data.requests

import com.google.gson.reflect.TypeToken
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.requests.interfaces.IRequestNoParams
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import java.lang.reflect.Type

class PlaylistGetPlaylists() : IRequestNoParams<List<PlaylistGetPlaylists.ResultItem>>,
    IRequestRefResult<List<PlaylistGetPlaylists.ResultItem>> {
    override val method: String = "Playlist.GetPlaylists"
    override val typeOfResult: Type =
        TypeToken.getParameterized(ArrayList::class.java, ResultItem::class.java).type

    data class ResultItem(
        override val playlistid: Int,
        val type: PlaylistType,
    ) : IHasPlaylistId
}
