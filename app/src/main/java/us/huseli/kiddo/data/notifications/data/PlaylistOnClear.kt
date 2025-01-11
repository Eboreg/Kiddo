package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class PlaylistOnClear(override val playlistid: Int) : INotificationData, IHasPlaylistId
