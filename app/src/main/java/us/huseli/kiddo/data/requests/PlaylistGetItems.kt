package us.huseli.kiddo.data.requests

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.AbstractListRequest
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.ListSort
import java.lang.reflect.Type

class PlaylistGetItems(
    val playlistId: Int,
    override val properties: List<ListFieldsAll> = listOf(
        ListFieldsAll.Artist,
        ListFieldsAll.CustomProperties,
        ListFieldsAll.Director,
        ListFieldsAll.Duration,
        ListFieldsAll.File,
        ListFieldsAll.Runtime,
        ListFieldsAll.Thumbnail,
        ListFieldsAll.Title,
    ),
    override val limits: ListLimits? = null,
    override val sort: ListSort? = null,
) : AbstractListRequest<ListItemAll>() {
    override val method: String = "Playlist.GetItems"
    override val typeOfResult: Type = Result::class.java

    override fun getParams() = super.getParams() + ("playlistid" to playlistId)

    @Immutable
    data class Result(
        override val items: List<ListItemAll>,
        override val limits: ListLimitsReturned,
    ) : IListResult<ListItemAll>
}
