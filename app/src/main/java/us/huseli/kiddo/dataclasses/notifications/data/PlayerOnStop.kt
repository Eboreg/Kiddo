package us.huseli.kiddo.dataclasses.notifications.data

import us.huseli.kiddo.dataclasses.types.NotificationsItem
import us.huseli.kiddo.interfaces.IHasPlayerSpeed

data class PlayerOnStop(
    val end: Boolean,
    val item: NotificationsItem,
) : IHasPlayerSpeed {
    override val speed: Double
        get() = 0.0
}
