package us.huseli.kiddo.data

import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.kiddo.data.types.ListItemAll

data class PlaylistWithItems(
    val playlist: PlaylistGetPlaylists.ResultItem,
    val items: List<ListItemAll>,
)
