package us.huseli.kiddo.data.notifications.data

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class VideoLibraryOnUpdate(
    val id: Int?,
    val type: Type?,
    val item: Item?,
    val added: Boolean?,
    val playcount: Int?,
    val transaction: Boolean?,
) : INotificationData {
    @Suppress("unused")
    enum class Type {
        @SerializedName("movie") Movie,
        @SerializedName("tvshow") TvShow,
        @SerializedName("episode") Episode,
        @SerializedName("musicvideo") MusicVideo,
    }

    data class Item(
        val id: Int,
        val type: Type,
    )

    fun theId(): Int? = id ?: item?.id
}
