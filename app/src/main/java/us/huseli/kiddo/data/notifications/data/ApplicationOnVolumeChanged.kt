package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class ApplicationOnVolumeChanged(
    val muted: Boolean,
    val volume: Int,
) : INotificationData
