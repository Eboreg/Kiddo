package us.huseli.kiddo.compose.controls

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Pause
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import us.huseli.kiddo.R

@Composable
fun PlayPauseButton(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = if (isPlaying) Icons.Sharp.Pause else Icons.Sharp.PlayArrow,
            contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play),
            modifier = modifier,
        )
    }
}
