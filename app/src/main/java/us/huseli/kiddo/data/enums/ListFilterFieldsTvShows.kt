package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.interfaces.IFilterField

@Suppress("unused")
enum class ListFilterFieldsTvShows : IFilterField {
    @SerializedName("actor") Actor,
    @SerializedName("dateadded") DateAdded,
    @SerializedName("director") Director,
    @SerializedName("genre") Genre,
    @SerializedName("inprogress") InProgress,
    @SerializedName("lastplayed") LastPlayed,
    @SerializedName("mpaarating") MpaaRating,
    @SerializedName("numepisodes") NumEpisodes,
    @SerializedName("numwatched") NumWatched,
    @SerializedName("originaltitle") OriginalTitle,
    @SerializedName("path") Path,
    @SerializedName("playcount") PlayCount,
    @SerializedName("playlist") Playlist,
    @SerializedName("plot") Plot,
    @SerializedName("rating") Rating,
    @SerializedName("status") Status,
    @SerializedName("studio") Studio,
    @SerializedName("tag") Tag,
    @SerializedName("title") Title,
    @SerializedName("userrating") UserRating,
    @SerializedName("virtualfolder") VirtualFolder,
    @SerializedName("votes") Votes,
    @SerializedName("year") Year,
}
