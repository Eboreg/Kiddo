package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.interfaces.IHasPlayerTime

data class PlayerNotificationsPlayerSeek(
    val playerid: Int,
    val speed: Int?,
    val seekoffset: GlobalTime?,
    override val time: GlobalTime?,
) : IHasPlayerTime
