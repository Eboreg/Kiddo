package us.huseli.kiddo.dataclasses.notifications.data

import us.huseli.kiddo.dataclasses.types.NotificationsItem
import us.huseli.kiddo.dataclasses.types.PlayerNotificationsPlayerSeek
import us.huseli.kiddo.interfaces.IHasPlayer

data class PlayerOnSeek(
    val item: NotificationsItem,
    override val player: PlayerNotificationsPlayerSeek,
) : IHasPlayer
