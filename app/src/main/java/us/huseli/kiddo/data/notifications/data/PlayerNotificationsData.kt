package us.huseli.kiddo.data.notifications.data

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.interfaces.IHasPlayerId
import us.huseli.kiddo.data.types.NotificationsItem
import us.huseli.kiddo.data.types.PlayerNotificationsPlayer
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

/**
 * Identical objects used for:
 *   Player.OnAVChange
 *   Player.OnAVStart
 *   Player.OnPause
 *   Player.OnPlay
 *   Player.OnResume
 *   Player.OnSpeedChanged
 */
@Immutable
data class PlayerNotificationsData(
    val item: NotificationsItem,
    val player: PlayerNotificationsPlayer,
) : INotificationData, IHasPlayerSpeed, IHasPlayerId {
    override val speed: Int?
        get() = player.speed
    override val playerid: Int?
        get() = player.playerid
}
