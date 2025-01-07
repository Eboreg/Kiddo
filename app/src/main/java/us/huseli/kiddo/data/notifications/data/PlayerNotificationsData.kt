package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.types.NotificationsItem
import us.huseli.kiddo.data.types.PlayerNotificationsPlayer
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayer
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
data class PlayerNotificationsData(
    val item: NotificationsItem,
    override val player: PlayerNotificationsPlayer,
) : IHasPlayer, INotificationData
