package us.huseli.kiddo.data.requests.interfaces

import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerId

interface IRequestHasPlayerId : IHasPlayerId {
    val playerId: Int
    override val playerid: Int
        get() = playerId
}
