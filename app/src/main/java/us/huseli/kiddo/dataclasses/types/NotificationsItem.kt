package us.huseli.kiddo.dataclasses.types

import com.google.gson.annotations.SerializedName

data class NotificationsItem(
    val album: String?,
    val artist: String?,
    val channeltype: PVRChannelType?,
    val episode: Int?,
    val file: String?,
    val id: Int?,
    val season: Int?,
    val showtitle: String?,
    val title: String?,
    val track: Int?,
    val type: NotificationsItemType,
    val year: Int?,
) {
    @Suppress("unused")
    enum class NotificationsItemType {
        @SerializedName("channel") Channel,
        @SerializedName("episode") Episode,
        @SerializedName("movie") Movie,
        @SerializedName("musicvideo") MusicVideo,
        @SerializedName("picture") Picture,
        @SerializedName("song") Song,
        @SerializedName("unknown") Unknown,
    }

    @Suppress("unused")
    enum class PVRChannelType {
        @SerializedName("radio") Radio,
        @SerializedName("tv") Tv,
    }
}
