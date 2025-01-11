package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class PlaylistOnRemove(
    override val playlistid: Int,
    val position: Int,
) : INotificationData, IHasPlaylistId
