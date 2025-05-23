package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.AbstractListMembers
import us.huseli.kiddo.data.enums.AudioAlbumReleaseType
import us.huseli.kiddo.data.types.interfaces.IListItemBase
import us.huseli.kiddo.data.types.interfaces.IListItemFile

@Immutable
data class ListItemFile(
    override val album: String?,
    override val albumartist: List<String>?,
    override val albumartistid: List<Int>?,
    override val albumid: Int?,
    override val albumlabel: String?,
    override val albumreleasetype: AudioAlbumReleaseType?,
    override val albumstatus: String?,
    override val art: MediaArtwork?,
    override val artist: List<String>?,
    override val artistid: List<Int>?,
    override val bitrate: Int?,
    override val bpm: Int?,
    override val cast: List<VideoCast>?,
    override val channels: Int?,
    override val comment: String?,
    override val compilation: Boolean?,
    override val contributors: List<AudioContributor>?,
    override val country: List<String>?,
    override val customproperties: Map<String, Any>?,
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
    override val episode: Int?,
    override val episodeguide: String?,
    override val fanart: String?,
    override val file: String,
    override val filetype: IListItemFile.FileType,
    override val firstaired: String?,
    override val genre: List<String>?,
    override val id: Int?,
    override val imdbnumber: String?,
    override val isboxset: Boolean?,
    override val label: String,
    override val lastmodified: String?,
    override val lastplayed: String?,
    override val lyrics: String?,
    override val mediapath: String?,
    override val mimetype: String?,
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
    override val releasetype: AudioAlbumReleaseType?,
    override val resume: VideoResume?,
    override val runtime: Int?,
    override val samplerate: Int?,
    override val season: Int?,
    override val set: String?,
    override val setid: Int?,
    override val showlink: List<String>?,
    override val showtitle: String?,
    override val size: Long?,
    override val songvideourl: String?,
    override val sortartist: String?,
    override val sorttitle: String?,
    override val specialsortepisode: Int?,
    override val specialsortseason: Int?,
    override val streamdetails: VideoStreams?,
    override val studio: List<String>?,
    override val style: List<String>?,
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
) : IListItemFile, AbstractListMembers() {
    override fun toString(): String = nonNullPropertiesToString()
}
