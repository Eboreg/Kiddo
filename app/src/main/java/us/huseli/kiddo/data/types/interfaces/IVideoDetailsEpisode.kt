package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.VideoCast
import us.huseli.retaintheme.extensions.takeIfNotBlank
import java.util.Locale

interface IVideoDetailsEpisode : IVideoDetailsFile {
    val cast: VideoCast?
    val episode: Int?
    val episodeid: Int
    val firstaired: String?
    val originaltitle: String?
    val productioncode: String?
    val rating: Double?
    val ratings: Any?
    val season: Int?
    val seasonid: Int?
    val showtitle: String?
    val specialsortepisode: Int?
    val specialsortseason: Int?
    val tvshowid: Int?
    val uniqueid: Map<String, String>?
    val userrating: Int?
    val votes: String?
    val writer: List<String>?

    @Suppress("LocalVariableName")
    fun getDisplayTitle(seasons: Int?): String {
        val _season = season
        val _episode = episode
        val _title = title?.takeIfNotBlank()

        val prefix =
            if (_season != null && _episode != null && seasons?.takeIf { it > 1 } != null)
                String.format(Locale.getDefault(), "S%02dE%02d", _season, _episode)
            else _episode?.toString()

        if (prefix != null) return _title?.let { "$prefix: $it" } ?: prefix
        return _title ?: label
    }
}
