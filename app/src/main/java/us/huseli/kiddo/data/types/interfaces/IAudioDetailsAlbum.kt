package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.enums.AudioAlbumReleaseType
import us.huseli.kiddo.data.types.AudioDetailsGenre
import us.huseli.kiddo.sensibleFormat
import us.huseli.retaintheme.extensions.cleanDuplicates
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import kotlin.time.Duration.Companion.seconds

interface IAudioDetailsAlbum : IAudioDetailsMedia {
    val albumduration: Int?
    val albumid: Int
    val albumlabel: String?
    val albumstatus: String?
    val compilation: Boolean?
    val description: String?
    val isboxset: Boolean?
    val lastplayed: String?
    val mood: List<String>?
    val musicbrainzalbumid: String?
    val musicbrainzreleasegroupid: String?
    val playcount: Int?
    val releasetype: AudioAlbumReleaseType?
    val songgenres: List<AudioDetailsGenre>?
    val sourceid: List<Int>?
    val style: List<String>?
    val theme: List<String>?
    val totaldiscs: Int?
    val type: String?

    val allGenres: Collection<String>
        get() = (songgenres?.mapNotNull { it.title } ?: emptyList())
            .plus(genre ?: emptyList())
            .cleanDuplicates()

    val supportingContent: String?
        get() = listOfNotNull(
            albumduration?.seconds?.sensibleFormat(),
            year?.takeIf { it > 0 }?.toString(),
        ).takeIfNotEmpty()?.joinToString(" · ")
}
