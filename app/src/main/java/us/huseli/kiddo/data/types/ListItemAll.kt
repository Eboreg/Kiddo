package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.AbstractListMembers
import us.huseli.kiddo.data.types.interfaces.IListItemAll
import us.huseli.kiddo.data.types.interfaces.IListItemBase
import us.huseli.kiddo.data.types.interfaces.IVideoDetailsFile
import us.huseli.kiddo.sensibleFormat
import us.huseli.kiddo.takeIfNotEmpty
import java.util.UUID
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Immutable
data class ListItemAll(
    override val album: String?,
    override val albumartist: List<String>?,
    override val albumartistid: List<Int>?,
    override val albumid: Int?,
    override val albumlabel: String?,
    override val albumreleasetype: IListItemBase.AudioAlbumReleaseType?,
    override val albumstatus: String?,
    override val art: MediaArtwork?,
    override val artist: List<String>?,
    override val artistid: List<Int>?,
    override val bitrate: Int?,
    override val bpm: Int?,
    override val cast: List<IListItemBase.VideoCast>?,
    override val channel: String?,
    override val channelnumber: Int?,
    override val channels: Int?,
    override val channeltype: IListItemAll.PVRChannelType?,
    override val comment: String?,
    override val compilation: Boolean?,
    override val contributors: List<IListItemBase.AudioContributor>?,
    override val country: List<String>?,
    override val customproperties: Map<String, String>?,
    override val dateadded: String?,
    override val description: String?,
    override val director: List<String>?,
    override val disc: Int?,
    override val disctitle: String?,
    override val displayartist: String?,
    override val displaycomposer: String?,
    override val displayconductor: String?,
    override val displaylyricist: String?,
    override val displayorchestra: String?,
    override val duration: Int?,
    override val dynpath: String?,
    override val endtime: String?,
    override val episode: Int?,
    override val episodeguide: String?,
    override val fanart: String?,
    override val file: String?,
    override val firstaired: String?,
    override val genre: List<String>?,
    override val hidden: Boolean?,
    override val id: Int?,
    override val imdbnumber: String?,
    override val isboxset: Boolean?,
    override val label: String,
    override val lastplayed: String?,
    override val locked: Boolean?,
    override val lyrics: String?,
    override val mediapath: String?,
    override val mood: List<String>?,
    override val mpaa: String?,
    override val musicbrainzalbumartistid: List<String>?,
    override val musicbrainzartistid: List<String>?,
    override val musicbrainztrackid: String?,
    override val originaldate: String?,
    override val originaltitle: String?,
    override val playcount: Int?,
    override val plot: String?,
    override val plotoutline: String?,
    override val premiered: String?,
    override val productioncode: String?,
    override val rating: Double?,
    override val releasedate: String?,
    override val releasetype: IListItemBase.AudioAlbumReleaseType?,
    override val resume: IVideoDetailsFile.VideoResume?,
    override val runtime: Int?,
    override val samplerate: Int?,
    override val season: Int?,
    override val set: String?,
    override val setid: Int?,
    override val showlink: List<String>?,
    override val showtitle: String?,
    override val songvideourl: String?,
    override val sortartist: String?,
    override val sorttitle: String?,
    override val specialsortepisode: Int?,
    override val specialsortseason: Int?,
    override val starttime: String?,
    override val streamdetails: IVideoDetailsFile.VideoStreams?,
    override val studio: List<String>?,
    override val style: List<String>?,
    override val subchannelnumber: Int?,
    override val tag: List<String>?,
    override val tagline: String?,
    override val theme: List<String>?,
    override val thumbnail: String?,
    override val title: String?,
    override val top250: Int?,
    override val totaldiscs: Int?,
    override val track: Int?,
    override val trailer: String?,
    override val tvshowid: Int?,
    override val type: IListItemBase.ItemType,
    override val uniqueid: Map<String, String>?,
    override val userrating: Int?,
    override val votes: String?,
    override val watchedepisodes: Int?,
    override val writer: List<String>?,
    override val year: Int?,
) : IListItemAll, AbstractListMembers() {
    val artistString: String?
        get() = artist?.takeIfNotEmpty()?.joinToString(", ")

    val directorString: String?
        get() = director?.takeIfNotEmpty()?.joinToString(", ")

    val stringId: String?
        get() = id?.toString()
            ?: file?.takeIfNotEmpty()
            ?: customproperties?.get("video_id")?.takeIfNotEmpty()
            ?: customproperties?.get("original_listitem_url")?.takeIfNotEmpty()

    val stringIdForced: String
        get() = stringId ?: UUID.randomUUID().toString()

    val supportingContent: String?
        get() = listOfNotNull(
            artistString,
            directorString,
            runtime?.toDuration(DurationUnit.SECONDS)?.sensibleFormat(),
            year?.toString(),
        ).takeIfNotEmpty()?.joinToString(" · ")

    override fun listMemberProperties(): List<Pair<String, Any?>> {
        return super.listMemberProperties().filterNot { it.first == "customproperties" }
    }

    override fun toString(): String = memberPropertiesToString()
}
