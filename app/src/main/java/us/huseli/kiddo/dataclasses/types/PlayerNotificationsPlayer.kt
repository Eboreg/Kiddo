package us.huseli.kiddo.dataclasses.types

import us.huseli.kiddo.interfaces.IPlayerNotificationsPlayer

data class PlayerNotificationsPlayer(
    override val playerid: Int,
    override val speed: Double?,
) : IPlayerNotificationsPlayer
