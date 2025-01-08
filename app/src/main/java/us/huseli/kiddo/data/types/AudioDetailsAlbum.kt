package us.huseli.kiddo.data.types

import us.huseli.kiddo.AbstractListMembers
import us.huseli.kiddo.data.enums.AudioAlbumReleaseType
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsAlbum
import us.huseli.kiddo.sensibleFormat
import us.huseli.kiddo.takeIfNotEmpty
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class AudioDetailsAlbum(
    override val albumid: Int,
    override val label: String,
    override val albumduration: Int? = null,
    override val albumlabel: String? = null,
    override val albumstatus: String? = null,
    override val compilation: Boolean? = null,
    override val description: String? = null,
    override val isboxset: Boolean? = null,
    override val lastplayed: String? = null,
    override val mood: List<String>? = null,
    override val musicbrainzalbumid: String? = null,
    override val musicbrainzreleasegroupid: String? = null,
    override val playcount: Int? = null,
    override val releasetype: AudioAlbumReleaseType? = null,
    override val songgenres: List<AudioDetailsGenre>? = null,
    override val sourceid: List<Int>? = null,
    override val style: List<String>? = null,
    override val theme: List<String>? = null,
    override val totaldiscs: Int? = null,
    override val type: String? = null,
    override val artist: List<String>? = null,
    override val artistid: List<Int>? = null,
    override val displayartist: String? = null,
    override val musicbrainzalbumartistid: List<String>? = null,
    override val originaldate: String? = null,
    override val rating: Double? = null,
    override val releasedate: String? = null,
    override val sortartist: String? = null,
    override val title: String? = null,
    override val userrating: Int? = null,
    override val votes: String? = null,
    override val year: Int? = null,
    override val art: MediaArtwork? = null,
    override val dateadded: String? = null,
    override val genre: List<String>? = null,
    override val fanart: String? = null,
    override val thumbnail: String? = null,
) : IAudioDetailsAlbum, AbstractListMembers() {
    val artistString: String?
        get() = artist?.takeIfNotEmpty()?.joinToString(", ")

    val supportingContent: String?
        get() = listOfNotNull(
            albumduration?.toDuration(DurationUnit.SECONDS)?.sensibleFormat(),
            year?.takeIf { it > 0 }?.toString(),
        ).takeIfNotEmpty()?.joinToString(" · ")

    override fun toString(): String = memberPropertiesToString()
}
