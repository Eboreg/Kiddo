package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.enums.AudioAlbumReleaseType
import us.huseli.kiddo.data.types.AudioContributor

interface IAudioDetailsSong : IAudioDetailsMedia {
    val album: String?
    val albumartist: List<String>?
    val albumartistid: List<Int>?
    val albumid: Int?
    val albumreleasetype: AudioAlbumReleaseType?
    val bitrate: Any?
    val bpm: Any?
    val channels: Any?
    val comment: String?
    val contributors: List<AudioContributor>?
    val disc: Int?
    val disctitle: String?
    val displaycomposer: String?
    val displayconductor: String?
    val displaylyricist: String?
    val displayorchestra: String?
    val duration: Int?
    val file: String?
    val genreid: List<Int>?
    val lastplayed: String?
    val lyrics: String?
    val mood: List<String>?
    val musicbrainzartistid: List<String>?
    val musicbrainztrackid: String?
    val playcount: Int?
    val samplerate: Any?
    val songid: Int
    val sourceid: List<Int>?
    val track: Int?
}
