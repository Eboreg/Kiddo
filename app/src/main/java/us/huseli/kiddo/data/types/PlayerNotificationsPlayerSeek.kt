package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.notifications.interfaces.IPlayerNotificationsPlayer

data class PlayerNotificationsPlayerSeek(
    override val playerid: Int,
    override val speed: Int?,
    val seekoffset: GlobalTime?,
    val time: GlobalTime?,
) : IPlayerNotificationsPlayer
