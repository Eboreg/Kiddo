package us.huseli.kiddo.data.notifications.data

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

@Immutable
data class ApplicationOnVolumeChanged(
    val muted: Boolean,
    val volume: Int,
) : INotificationData
