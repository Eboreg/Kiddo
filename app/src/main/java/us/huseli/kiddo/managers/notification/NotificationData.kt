package us.huseli.kiddo.managers.notification

import android.app.Notification

sealed class NotificationData {
    abstract val id: Int

    data class Add(
        override val id: Int,
        val title: String,
        val ongoing: Boolean,
        val text: String? = null,
        val progress: Int? = null,
        val action: Notification.Action? = null,
    ) : NotificationData()

    data class Update(
        override val id: Int,
        val progress: Int? = null,
        val title: String? = null,
        val text: String? = null,
    ) : NotificationData()

    data class Cancel(override val id: Int) : NotificationData()
}
