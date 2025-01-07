package us.huseli.kiddo.data.uistates

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.takeIfNotEmpty
import us.huseli.retaintheme.extensions.sensibleFormat
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Immutable
data class PlayerItemUiState(val item: ListItemAll, val properties: PlayerPropertyValue?) {
    val artist: String?
        get() = item.artist?.takeIfNotEmpty()?.joinToString(", ")

    val director: String?
        get() = item.director?.takeIfNotEmpty()?.joinToString(", ")

    val duration: Duration?
        get() = item.runtime?.toDuration(DurationUnit.SECONDS)

    val subtitleEnabled: Boolean
        get() = properties?.subtitleenabled == true

    val supportingContent: String?
        get() = listOfNotNull(artist, director, duration?.sensibleFormat()).takeIfNotEmpty()?.joinToString(" · ")

    val title: String
        get() = item.title?.takeIfNotBlank() ?: item.label
}
