package us.huseli.kiddo.data.notifications.data

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

@Immutable
data class PlaylistOnRemove(
    override val playlistid: Int,
    val position: Int,
) : INotificationData, IHasPlaylistId
