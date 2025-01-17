package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.interfaces.IFilterField

@Suppress("unused")
enum class ListFilterFieldsSongs : IFilterField {
    @SerializedName("album") Album,
    @SerializedName("albumartist") AlbumArtist,
    @SerializedName("artist") Artist,
    @SerializedName("bitrate") Bitrate,
    @SerializedName("bpm") Bpm,
    @SerializedName("channels") Channels,
    @SerializedName("comment") Comment,
    @SerializedName("dateadded") DateAdded,
    @SerializedName("datemodified") DateModified,
    @SerializedName("datenew") DateNew,
    @SerializedName("disctitle") DiscTitle,
    @SerializedName("filename") Filename,
    @SerializedName("genre") Genre,
    @SerializedName("lastplayed") LastPlayed,
    @SerializedName("moods") Moods,
    @SerializedName("originalyear") OriginalYear,
    @SerializedName("path") Path,
    @SerializedName("playcount") PlayCount,
    @SerializedName("playlist") Playlist,
    @SerializedName("rating") Rating,
    @SerializedName("samplerate") SampleRate,
    @SerializedName("source") Source,
    @SerializedName("time") Time,
    @SerializedName("title") Title,
    @SerializedName("tracknumber") TrackNumber,
    @SerializedName("userrating") UserRating,
    @SerializedName("virtualfolder") VirtualFolder,
    @SerializedName("year") Year,
}
