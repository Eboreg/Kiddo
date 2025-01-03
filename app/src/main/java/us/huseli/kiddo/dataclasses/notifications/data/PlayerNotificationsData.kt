package us.huseli.kiddo.dataclasses.notifications.data

import us.huseli.kiddo.dataclasses.types.NotificationsItem
import us.huseli.kiddo.dataclasses.types.PlayerNotificationsPlayer
import us.huseli.kiddo.interfaces.IHasPlayer

/**
 * Identical objects used for Player.OnAVChange, Player.OnAVStart, Player.OnPause, Player.OnPlay, and Player.OnResume.
 */
data class PlayerNotificationsData(
    val item: NotificationsItem,
    override val player: PlayerNotificationsPlayer,
) : IHasPlayer
