package us.huseli.kiddo.data.types.interfaces

interface IAudioDetailsMedia : IAudioDetailsBase {
    val artist: List<String>?
    val artistid: List<Int>?
    val displayartist: String?
    val musicbrainzalbumartistid: List<String>?
    val originaldate: String?
    val rating: Double?
    val releasedate: String?
    val sortartist: String?
    val title: String?
    val userrating: Int?
    val votes: String?
    val year: Int?
}
