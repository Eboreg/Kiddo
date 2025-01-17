package us.huseli.kiddo.compose.screens.albumdetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.data.types.AudioDetailsSong
import us.huseli.kiddo.sensibleFormat
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import kotlin.time.Duration.Companion.seconds

@Composable
fun AlbumDetailsSong(
    index: Int,
    song: AudioDetailsSong,
    albumArtists: List<String>?,
    albumYear: Int?,
    totalDiscs: Int? = null,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val track = song.getTrackNumber(totalDiscs)

    ListItem(
        modifier = modifier.clickable(onClick = onPlayClick),
        tonalElevation = 10.dp * ((index + 1) % 2),
        leadingContent = {
            track?.also {
                val trackLength = if ((song.track ?: 0) < 10) track.length + 2 else track.length + 1
                val charWidth = with(LocalDensity.current) { LocalTextStyle.current.fontSize.toDp() * 0.5f }

                Text(text = it, modifier = Modifier.widthIn(min = charWidth * trackLength))
            }
        },
        headlineContent = {
            Text(text = song.displayTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
        },
        supportingContent = {
            song.artist?.takeIfNotEmpty()?.also { artists ->
                if (artists != albumArtists) Text(artists.joinToString(" / "))
            }
        },
        trailingContent = {
            Column(horizontalAlignment = Alignment.End) {
                song.year?.takeIf { it > 0 && it != albumYear }?.also { Text(it.toString()) }
                song.duration?.takeIf { it > 0 }?.seconds?.sensibleFormat()?.also { Text(it) }
            }
        }
    )
}
