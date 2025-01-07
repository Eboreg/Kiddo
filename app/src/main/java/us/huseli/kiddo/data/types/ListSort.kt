package us.huseli.kiddo.data.types

import com.google.gson.annotations.SerializedName

data class ListSort(
    val ignorearticle: Boolean? = null,
    val method: Method? = null,
    val order: Order? = null,
    val useartistsortname: Boolean? = null,
) {
    @Suppress("unused")
    enum class Method {
        @SerializedName("album") Album,
        @SerializedName("albumtype") AlbumType,
        @SerializedName("artist") Artist,
        @SerializedName("bitrate") Bitrate,
        @SerializedName("bpm") Bpm,
        @SerializedName("country") Country,
        @SerializedName("date") Date,
        @SerializedName("dateadded") DateAdded,
        @SerializedName("drivetype") DriveType,
        @SerializedName("episode") Episode,
        @SerializedName("file") File,
        @SerializedName("genre") Genre,
        @SerializedName("label") Label,
        @SerializedName("lastplayed") LastPlayed,
        @SerializedName("listeners") Listeners,
        @SerializedName("mpaa") Mpaa,
        @SerializedName("none") None,
        @SerializedName("originaldate") OriginalDate,
        @SerializedName("originaltitle") OriginalTitle,
        @SerializedName("path") Path,
        @SerializedName("playcount") PlayCount,
        @SerializedName("playlist") Playlist,
        @SerializedName("productioncode") ProductionCode,
        @SerializedName("programcount") ProgramCount,
        @SerializedName("random") Random,
        @SerializedName("rating") Rating,
        @SerializedName("season") Season,
        @SerializedName("size") Size,
        @SerializedName("sorttitle") SortTitle,
        @SerializedName("studio") Studio,
        @SerializedName("time") Time,
        @SerializedName("title") Title,
        @SerializedName("top250") Top250,
        @SerializedName("totaldiscs") TotalDiscs,
        @SerializedName("totalepisodes") TotalEpisodes,
        @SerializedName("track") Track,
        @SerializedName("tvshowstatus") TvShowStatus,
        @SerializedName("tvshowtitle") TvShowTitle,
        @SerializedName("userrating") UserRating,
        @SerializedName("votes") Votes,
        @SerializedName("watchedepisodes") WatchedEpisodes,
        @SerializedName("year") Year,
    }

    @Suppress("unused")
    enum class Order {
        @SerializedName("ascending") Ascending,
        @SerializedName("descending") Descending,
    }
}
