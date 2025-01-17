package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.interfaces.IHasPlayerTime

@Immutable
data class PlayerNotificationsPlayerSeek(
    val playerid: Int,
    val speed: Int?,
    val seekoffset: GlobalTime?,
    override val time: GlobalTime?,
) : IHasPlayerTime
