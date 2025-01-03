package us.huseli.kiddo.dataclasses.types

import us.huseli.kiddo.interfaces.IPlayerNotificationsPlayer

data class PlayerNotificationsPlayerSeek(
    override val playerid: Int,
    override val speed: Double?,
    val seekoffset: GlobalTime?,
    val time: GlobalTime?,
) : IPlayerNotificationsPlayer
