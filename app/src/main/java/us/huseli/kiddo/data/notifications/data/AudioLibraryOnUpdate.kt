package us.huseli.kiddo.data.notifications.data

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

@Immutable
data class AudioLibraryOnUpdate(
    val added: Boolean?,
    val id: Int,
    val transaction: Boolean?,
    val type: String,
) : INotificationData
