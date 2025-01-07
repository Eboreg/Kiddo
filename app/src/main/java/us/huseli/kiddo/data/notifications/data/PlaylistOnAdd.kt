package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.notifications.interfaces.INotificationData
import us.huseli.kiddo.data.types.NotificationsItem

data class PlaylistOnAdd(
    val item: NotificationsItem,
    val playlistid: Int,
    val position: Int,
) : INotificationData
