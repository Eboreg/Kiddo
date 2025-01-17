package us.huseli.kiddo.managers.websocket

import us.huseli.kiddo.data.notifications.Notification

fun interface KodiNotificationListener {
    fun onKodiNotification(notification: Notification<*>)
}
