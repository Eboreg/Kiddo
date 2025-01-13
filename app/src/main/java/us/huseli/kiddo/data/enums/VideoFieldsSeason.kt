package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class VideoFieldsSeason {
    @SerializedName("art") Art,
    @SerializedName("episode") Episode,
    @SerializedName("fanart") Fanart,
    @SerializedName("playcount") PlayCount,
    @SerializedName("season") Season,
    @SerializedName("showtitle") ShowTitle,
    @SerializedName("thumbnail") Thumbnail,
    @SerializedName("title") Title,
    @SerializedName("tvshowid") TvShowId,
    @SerializedName("userrating") UserRating,
    @SerializedName("watchedepisodes") WatchedEpisodes,
}
