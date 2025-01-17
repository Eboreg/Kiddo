package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.interfaces.IFilterField

@Suppress("unused")
enum class ListFilterFieldsAlbums : IFilterField {
    @SerializedName("album") Album,
    @SerializedName("albumartist") AlbumArtist,
    @SerializedName("albumduration") AlbumDuration,
    @SerializedName("albumstatus") AlbumStatus,
    @SerializedName("artist") Artist,
    @SerializedName("compilation") Compilation,
    @SerializedName("dateadded") DateAdded,
    @SerializedName("datemodified") DateModified,
    @SerializedName("datenew") DateNew,
    @SerializedName("disctitle") DiscTitle,
    @SerializedName("genre") Genre,
    @SerializedName("isboxset") IsBoxSet,
    @SerializedName("label") Label,
    @SerializedName("lastplayed") LastPlayed,
    @SerializedName("moods") Moods,
    @SerializedName("originalyear") OriginalYear,
    @SerializedName("path") Path,
    @SerializedName("playcount") PlayCount,
    @SerializedName("playlist") Playlist,
    @SerializedName("rating") Rating,
    @SerializedName("review") Review,
    @SerializedName("source") Source,
    @SerializedName("styles") Styles,
    @SerializedName("themes") Themes,
    @SerializedName("totaldiscs") TotalDiscs,
    @SerializedName("type") Type,
    @SerializedName("userrating") UserRating,
    @SerializedName("virtualfolder") VirtualFolder,
    @SerializedName("year") Year,
}
