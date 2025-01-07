package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.notifications.interfaces.IPlayerNotificationsPlayer

data class PlayerNotificationsPlayer(
    override val playerid: Int,
    override val speed: Int?,
) : IPlayerNotificationsPlayer
