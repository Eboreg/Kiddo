package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.FastForward
import androidx.compose.material.icons.sharp.FastRewind
import androidx.compose.material.icons.sharp.SkipNext
import androidx.compose.material.icons.sharp.SkipPrevious
import androidx.compose.material.icons.sharp.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.PlayPauseButton
import us.huseli.kiddo.data.types.PlayerPropertyValue

@Composable
fun PlayerPanelPlaybackButtonRow(
    properties: PlayerPropertyValue?,
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onGotoPreviousClick: () -> Unit,
    onFastRewindClick: () -> Unit,
    onPlayOrPauseClick: () -> Unit,
    onFastForwardClick: () -> Unit,
    onStopClick: () -> Unit,
    onGotoNextClick: () -> Unit,
) {
    val playbackIconModifier = Modifier.size(30.dp)

    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier,
    ) {
        IconButton(onClick = onGotoPreviousClick) {
            Icon(
                imageVector = Icons.Sharp.SkipPrevious,
                contentDescription = stringResource(R.string.skip_to_previous),
                modifier = playbackIconModifier,
            )
        }
        IconButton(
            onClick = onFastRewindClick,
            enabled = properties?.canchangespeed == true,
        ) {
            Icon(
                imageVector = Icons.Sharp.FastRewind,
                contentDescription = stringResource(R.string.decrease_speed),
                modifier = playbackIconModifier,
            )
        }
        PlayPauseButton(
            isPlaying = isPlaying,
            onClick = onPlayOrPauseClick,
            modifier = playbackIconModifier,
        )
        IconButton(onClick = onStopClick) {
            Icon(Icons.Sharp.Stop, stringResource(R.string.stop), modifier = playbackIconModifier)
        }
        IconButton(
            onClick = onFastForwardClick,
            enabled = properties?.canchangespeed == true,
        ) {
            Icon(
                imageVector = Icons.Sharp.FastForward,
                contentDescription = stringResource(R.string.increase_speed),
                modifier = playbackIconModifier,
            )
        }
        IconButton(onClick = onGotoNextClick) {
            Icon(
                Icons.Sharp.SkipNext,
                stringResource(R.string.skip_to_next),
                modifier = playbackIconModifier
            )
        }
    }
}
