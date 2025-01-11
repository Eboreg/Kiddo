package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.types.NotificationsItem
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.interfaces.IHasPlayerId
import us.huseli.kiddo.data.interfaces.IHasPlayerTime
import us.huseli.kiddo.data.notifications.interfaces.INotificationData
import us.huseli.kiddo.data.types.GlobalTime

data class PlayerOnStop(
    val end: Boolean,
    val item: NotificationsItem,
) : IHasPlayerSpeed, INotificationData, IHasPlayerId, IHasPlayerTime {
    override val speed: Int
        get() = 0
    override val playerid: Int?
        get() = null
    override val time: GlobalTime?
        get() = GlobalTime()
}
