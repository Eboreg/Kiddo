package us.huseli.kiddo.data.types.interfaces

import us.huseli.retaintheme.extensions.takeIfNotBlank
import java.util.Locale

interface IVideoDetailsSeason : IVideoDetailsBase {
    val episode: Int?
    val season: Int
    val seasonid: Int
    val showtitle: String?
    val title: String?
    val tvshowid: Int?
    val userrating: Int?
    val watchedepisodes: Int?

    val displayTitle: String
        get() = formattedNumber + (title?.takeIfNotBlank()?.let { ": $it" } ?: "")

    val formattedNumber: String
        get() = String.format(Locale.getDefault(), "S%02d", season)

    val progress: Float
        get() = episode?.takeIf { it > 0 }?.let { watchedepisodes?.toFloat()?.div(it) } ?: 0f
}
