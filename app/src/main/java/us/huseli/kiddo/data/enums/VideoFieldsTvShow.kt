package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.requests.interfaces.IRequestProperty

@Suppress("unused")
enum class VideoFieldsTvShow : IRequestProperty {
    @SerializedName("art") Art,
    @SerializedName("cast") Cast,
    @SerializedName("dateadded") DateAdded,
    @SerializedName("episode") Episode,
    @SerializedName("episodeguide") EpisodeGuide,
    @SerializedName("fanart") Fanart,
    @SerializedName("file") File,
    @SerializedName("genre") Genre,
    @SerializedName("imdbnumber") ImdbNumber,
    @SerializedName("lastplayed") LastPlayed,
    @SerializedName("mpaa") Mpaa,
    @SerializedName("originaltitle") OriginalTitle,
    @SerializedName("playcount") PlayCount,
    @SerializedName("plot") Plot,
    @SerializedName("premiered") Premiered,
    @SerializedName("rating") Rating,
    @SerializedName("ratings") Ratings,
    @SerializedName("runtime") Runtime,
    @SerializedName("season") Season,
    @SerializedName("sorttitle") SortTitle,
    @SerializedName("studio") Studio,
    @SerializedName("tag") Tag,
    @SerializedName("thumbnail") Thumbnail,
    @SerializedName("title") Title,
    @SerializedName("uniqueid") UniqueId,
    @SerializedName("userrating") UserRating,
    @SerializedName("votes") Votes,
    @SerializedName("watchedepisodes") WatchedEpisodes,
    @SerializedName("year") Year,
}
