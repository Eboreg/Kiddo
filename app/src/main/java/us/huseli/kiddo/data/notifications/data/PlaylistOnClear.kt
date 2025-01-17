package us.huseli.kiddo.data.notifications.data

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.interfaces.IHasPlaylistId
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

@Immutable
data class PlaylistOnClear(override val playlistid: Int) : INotificationData, IHasPlaylistId
