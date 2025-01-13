package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class VideoFieldsEpisode {
    @SerializedName("art") Art,
    @SerializedName("cast") Cast,
    @SerializedName("dateadded") DateAdded,
    @SerializedName("director") Director,
    @SerializedName("episode") Episode,
    @SerializedName("fanart") FanArt,
    @SerializedName("file") File,
    @SerializedName("firstaired") FirstAired,
    @SerializedName("lastplayed") LastPlayed,
    @SerializedName("originaltitle") OriginalTitle,
    @SerializedName("playcount") PlayCount,
    @SerializedName("plot") Plot,
    @SerializedName("productioncode") ProductionCode,
    @SerializedName("rating") Rating,
    @SerializedName("ratings") Ratings,
    @SerializedName("resume") Resume,
    @SerializedName("runtime") Runtime,
    @SerializedName("season") Season,
    @SerializedName("seasonid") SeasonId,
    @SerializedName("showtitle") ShowTitle,
    @SerializedName("specialsortepisode") SpecialSortEpisode,
    @SerializedName("specialsortseason") SpecialSortSeason,
    @SerializedName("streamdetails") StreamDetails,
    @SerializedName("thumbnail") Thumbnail,
    @SerializedName("title") Title,
    @SerializedName("tvshowid") TvShowId,
    @SerializedName("uniqueid") UniqueId,
    @SerializedName("userrating") UserRating,
    @SerializedName("votes") Votes,
    @SerializedName("writer") Writer,
}
