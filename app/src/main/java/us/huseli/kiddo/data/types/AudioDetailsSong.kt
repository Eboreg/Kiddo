package us.huseli.kiddo.data.types

import us.huseli.kiddo.AbstractListMembers
import us.huseli.kiddo.data.enums.AudioAlbumReleaseType
import us.huseli.kiddo.data.types.interfaces.IAudioDetailsSong
import us.huseli.retaintheme.extensions.takeIfNotBlank

data class AudioDetailsSong(
    override val album: String? = null,
    override val albumartist: List<String>? = null,
    override val albumartistid: List<Int>? = null,
    override val albumid: Int? = null,
    override val albumreleasetype: AudioAlbumReleaseType? = null,
    override val art: MediaArtwork? = null,
    override val artist: List<String>? = null,
    override val artistid: List<Int>? = null,
    override val bitrate: Any? = null,
    override val bpm: Any? = null,
    override val channels: Any? = null,
    override val comment: String? = null,
    override val contributors: List<AudioContributor>? = null,
    override val dateadded: String? = null,
    override val disc: Int? = null,
    override val disctitle: String? = null,
    override val displayartist: String? = null,
    override val displaycomposer: String? = null,
    override val displayconductor: String? = null,
    override val displaylyricist: String? = null,
    override val displayorchestra: String? = null,
    override val duration: Int? = null,
    override val fanart: String? = null,
    override val file: String? = null,
    override val genre: List<String>? = null,
    override val genreid: List<Int>? = null,
    override val label: String,
    override val lastplayed: String? = null,
    override val lyrics: String? = null,
    override val mood: List<String>? = null,
    override val musicbrainzalbumartistid: List<String>? = null,
    override val musicbrainzartistid: List<String>? = null,
    override val musicbrainztrackid: String? = null,
    override val originaldate: String? = null,
    override val playcount: Int? = null,
    override val rating: Double? = null,
    override val releasedate: String? = null,
    override val samplerate: Any? = null,
    override val songid: Int,
    override val sortartist: String? = null,
    override val sourceid: List<Int>? = null,
    override val thumbnail: String? = null,
    override val title: String? = null,
    override val track: Int? = null,
    override val userrating: Int? = null,
    override val votes: String? = null,
    override val year: Int? = null,
) : IAudioDetailsSong, AbstractListMembers() {
    val displayTitle: String
        get() = title?.takeIfNotBlank() ?: label

    override fun toString(): String = memberPropertiesToString()
}
