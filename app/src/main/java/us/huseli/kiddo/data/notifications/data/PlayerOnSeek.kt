package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.types.NotificationsItem
import us.huseli.kiddo.data.types.PlayerNotificationsPlayerSeek
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayer
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class PlayerOnSeek(
    val item: NotificationsItem,
    override val player: PlayerNotificationsPlayerSeek,
) : IHasPlayer, INotificationData
