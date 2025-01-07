package us.huseli.kiddo.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.sharp.FastForward
import androidx.compose.material.icons.sharp.FastRewind
import androidx.compose.material.icons.sharp.Fullscreen
import androidx.compose.material.icons.sharp.Pause
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material.icons.sharp.Stop
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.AudioStreamMenuButton
import us.huseli.kiddo.compose.controls.HorizontalVolumeControl
import us.huseli.kiddo.compose.controls.PlayerTimeSlider
import us.huseli.kiddo.compose.controls.SubtitleMenuButton
import us.huseli.kiddo.data.types.interfaces.IListItemBase
import us.huseli.kiddo.data.uistates.PlayerItemUiState
import us.huseli.kiddo.viewmodels.PlayerPanelViewModel

@Composable
fun PlayerPanel(
    state: PlayerItemUiState,
    onMovieDetailsClick: (Int) -> Unit,
    viewModel: PlayerPanelViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val isFullscreen by viewModel.isFullscreen.collectAsStateWithLifecycle()
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val elapsedTime = viewModel.playerElapsedTime.collectAsStateWithLifecycle()
    val progress = viewModel.playerProgress.collectAsStateWithLifecycle()
    val totalTime = viewModel.playerTotalTime.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val thumbnail by viewModel.playerThumbnail.collectAsStateWithLifecycle()

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .height(100.dp)
                .padding(10.dp)
                .fillMaxWidth()
                .then(
                    state.item.id?.takeIf { state.item.type == IListItemBase.ItemType.Movie }?.let { movieId ->
                        Modifier.clickable { onMovieDetailsClick(movieId) }
                    } ?: Modifier
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ) {
                thumbnail?.also {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } ?: run {
                    Icon(
                        imageVector = Icons.Outlined.ImageNotSupported,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxSize(0.8f)
                            .align(Alignment.Center),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxHeight()) {
                Text(state.title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                state.supportingContent?.also {
                    Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Row {
                IconButton(onClick = { viewModel.decreasePlayerSpeed() }) {
                    Icon(Icons.Sharp.FastRewind, stringResource(R.string.decrease_speed))
                }
                IconButton(onClick = { viewModel.playOrPause() }) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Sharp.Pause else Icons.Sharp.PlayArrow,
                        contentDescription = stringResource(if (isPlaying) R.string.pause else R.string.play),
                    )
                }
                IconButton(onClick = { viewModel.stop() }) { Icon(Icons.Sharp.Stop, stringResource(R.string.stop)) }
                IconButton(onClick = { viewModel.increasePlayerSpeed() }) {
                    Icon(Icons.Sharp.FastForward, stringResource(R.string.increase_speed))
                }
            }
            Row {
                FilledIconToggleButton(
                    checked = isFullscreen,
                    onCheckedChange = { viewModel.toggleFullscreen() },
                    content = { Icon(Icons.Sharp.Fullscreen, stringResource(R.string.toggle_fullscreen)) },
                )
                SubtitleMenuButton(
                    state = state,
                    onDisableSubtitleClick = { viewModel.disableSubtitle() },
                    onSetSubtitleClick = { viewModel.setSubtitle(it) },
                    onSubtitleSearchClick = { viewModel.openSubtitleSearch() },
                )
                AudioStreamMenuButton(state = state, onSetStreamClick = { viewModel.setAudioStream(it) })
            }
        }

        HorizontalVolumeControl(
            volume = volume,
            isMuted = isMuted,
            onVolumeChange = { viewModel.setVolume(it) },
            onIsMutedChange = { viewModel.setMute(it) },
        )

        PlayerTimeSlider(
            elapsedTime = elapsedTime,
            progress = progress,
            totalTime = totalTime,
            onSeekInProgress = { viewModel.seekInProgress(it) },
            onSeekFinish = { viewModel.seekFinish(progress.value) },
        )
    }
}
