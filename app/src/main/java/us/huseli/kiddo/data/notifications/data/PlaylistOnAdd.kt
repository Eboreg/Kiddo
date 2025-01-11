package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.notifications.interfaces.INotificationData
import us.huseli.kiddo.data.types.NotificationsItem

data class PlaylistOnAdd(
    val item: NotificationsItem,
    override val playlistid: Int,
    val position: Int,
) : INotificationData, IHasPlaylistId
