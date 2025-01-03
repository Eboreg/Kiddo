package us.huseli.kiddo.dataclasses.notifications.data

import us.huseli.kiddo.dataclasses.types.NotificationsItem

data class PlaylistOnAdd(
    val item: NotificationsItem,
    val playlistid: Int,
    val position: Int,
)
