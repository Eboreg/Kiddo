package us.huseli.kiddo.data.types.interfaces

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.enums.AudioAlbumReleaseType
import us.huseli.kiddo.data.interfaces.IHasPlayerTotalTime
import us.huseli.kiddo.data.types.AudioContributor
import us.huseli.kiddo.data.types.GlobalTime
import us.huseli.kiddo.data.types.VideoCast

interface IListItemBase : IVideoDetailsFile, IAudioDetailsMedia, IHasPlayerTotalTime {
    val album: String?
    val albumartist: List<String>?
    val albumartistid: List<Int>?
    val albumid: Int?
    val albumlabel: String?
    val albumreleasetype: AudioAlbumReleaseType?
    val albumstatus: String?
    val bitrate: Int?
    val bpm: Int?
    val cast: List<VideoCast>?
    val channels: Int?
    val comment: String?
    val compilation: Boolean?
    val contributors: List<AudioContributor>?
    val country: List<String>?
    val customproperties: Map<String, Any>?
    val description: String?
    val disc: Int?
    val disctitle: String?
    val displaycomposer: String?
    val displayconductor: String?
    val displaylyricist: String?
    val displayorchestra: String?
    val duration: Int?
    val dynpath: String?
    val episode: Int?
    val episodeguide: String?
    val firstaired: String?
    val id: Int?
    val imdbnumber: String?
    val isboxset: Boolean?
    val lyrics: String?
    val mediapath: String?
    val mood: List<String>?
    val mpaa: String?
    val musicbrainzartistid: List<String>?
    val musicbrainztrackid: String?
    override val originaldate: String?
    val originaltitle: String?
    val plotoutline: String?
    val premiered: String?
    val productioncode: String?
    override val releasedate: String?
    val releasetype: AudioAlbumReleaseType?
    val samplerate: Int?
    val season: Int?
    val set: String?
    val setid: Int?
    val showlink: List<String>?
    val showtitle: String?
    val songvideourl: String?
    val sorttitle: String?
    val specialsortepisode: Int?
    val specialsortseason: Int?
    val studio: List<String>?
    val style: List<String>?
    val tag: List<String>?
    val tagline: String?
    val theme: List<String>?
    val top250: Int?
    val totaldiscs: Int?
    val track: Int?
    val trailer: String?
    val tvshowid: Int?
    val type: ItemType
    val uniqueid: Map<String, String>?
    override val votes: String?
    val watchedepisodes: Int?
    val writer: List<String>?

    @Suppress("unused")
    enum class ItemType {
        @SerializedName("channel") Channel,
        @SerializedName("episode") Episode,
        @SerializedName("movie") Movie,
        @SerializedName("musicvideo") MusicVideo,
        @SerializedName("picture") Picture,
        @SerializedName("recording") Recording,
        @SerializedName("song") Song,
        @SerializedName("unknown") Unknown,
    }

    override val totaltime: GlobalTime?
        get() = (duration ?: runtime)?.let { seconds ->
            GlobalTime(
                hours = seconds / 60 / 60,
                milliseconds = 0,
                minutes = seconds / 60 % 60,
                seconds = seconds % 60,
            )
        }
}
