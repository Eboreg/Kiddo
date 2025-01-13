package us.huseli.kiddo.data.notifications.data

import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class AudioLibraryOnUpdate(
    val added: Boolean?,
    val id: Int,
    val transaction: Boolean?,
    val type: String,
) : INotificationData
