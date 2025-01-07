package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.types.NotificationsItem
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class PlayerOnStop(
    val end: Boolean,
    val item: NotificationsItem,
) : IHasPlayerSpeed, INotificationData {
    override val speed: Int
        get() = 0
}
