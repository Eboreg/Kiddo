package us.huseli.kiddo.data.notifications.data

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.interfaces.IHasPlayerId
import us.huseli.kiddo.data.notifications.interfaces.INotificationData
import us.huseli.kiddo.data.types.PlayerNotificationsPlayer
import us.huseli.kiddo.data.types.PlayerPropertyValue

@Immutable
data class PlayerOnPropertyChanged(
    val player: PlayerNotificationsPlayer,
    val property: PlayerPropertyValue,
) : INotificationData, IHasPlayerSpeed, IHasPlayerId {
    override val speed: Int?
        get() = player.speed
    override val playerid: Int
        get() = player.playerid
}
