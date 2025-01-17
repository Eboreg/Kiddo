package us.huseli.kiddo.data.notifications.data

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.interfaces.IHasPlayerId
import us.huseli.kiddo.data.interfaces.IHasPlayerTime
import us.huseli.kiddo.data.types.NotificationsItem
import us.huseli.kiddo.data.types.PlayerNotificationsPlayerSeek
import us.huseli.kiddo.data.notifications.interfaces.INotificationData
import us.huseli.kiddo.data.types.GlobalTime

@Immutable
data class PlayerOnSeek(
    val item: NotificationsItem,
    val player: PlayerNotificationsPlayerSeek,
) : INotificationData, IHasPlayerSpeed, IHasPlayerId, IHasPlayerTime {
    override val speed: Int?
        get() = player.speed
    override val playerid: Int
        get() = player.playerid
    override val time: GlobalTime?
        get() = player.time
}
