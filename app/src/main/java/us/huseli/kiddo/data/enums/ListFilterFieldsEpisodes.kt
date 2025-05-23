package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.interfaces.IFilterField

@Suppress("unused")
enum class ListFilterFieldsEpisodes : IFilterField {
    @SerializedName("actor") Actor,
    @SerializedName("airdate") AirDate,
    @SerializedName("audiochannels") AudioChannels,
    @SerializedName("audiocodec") AudioCodec,
    @SerializedName("audiocount") AudioCount,
    @SerializedName("audiolanguage") AudioLanguage,
    @SerializedName("dateadded") DateAdded,
    @SerializedName("director") Director,
    @SerializedName("episode") Episode,
    @SerializedName("filename") Filename,
    @SerializedName("genre") Genre,
    @SerializedName("inprogress") InProgress,
    @SerializedName("lastplayed") LastPlayed,
    @SerializedName("mpaarating") MpaaRating,
    @SerializedName("originaltitle") OriginalTitle,
    @SerializedName("path") Path,
    @SerializedName("playcount") PlayCount,
    @SerializedName("playlist") Playlist,
    @SerializedName("plot") Plot,
    @SerializedName("rating") Rating,
    @SerializedName("season") Season,
    @SerializedName("studio") Studio,
    @SerializedName("subtitlecount") SubtitleCount,
    @SerializedName("subtitlelanguage") SubtitleLanguage,
    @SerializedName("tag") Tag,
    @SerializedName("time") Time,
    @SerializedName("title") Title,
    @SerializedName("tvshow") TvShow,
    @SerializedName("userrating") UserRating,
    @SerializedName("videoaspect") VideoAspect,
    @SerializedName("videocodec") VideoCodec,
    @SerializedName("videoresolution") VideoResolution,
    @SerializedName("virtualfolder") VirtualFolder,
    @SerializedName("votes") Votes,
    @SerializedName("writers") Writers,
    @SerializedName("year") Year,
}
