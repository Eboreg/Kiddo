package us.huseli.kiddo.compose

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ImageNotSupported
import androidx.compose.material.icons.sharp.Fullscreen
import androidx.compose.material.icons.sharp.Repeat
import androidx.compose.material.icons.sharp.RepeatOne
import androidx.compose.material.icons.sharp.Shuffle
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.PlayPauseButton
import us.huseli.kiddo.compose.controls.PlayerTimeSlider
import us.huseli.kiddo.compose.controls.ToggleChip
import us.huseli.kiddo.data.enums.PlayerRepeat
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.kiddo.data.types.interfaces.IListItemAll
import us.huseli.kiddo.data.types.interfaces.IListItemBase
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.viewmodels.PlayerPanelViewModel
import kotlin.math.sqrt

enum class PanelCollapseStatus { Collapsed, Expanded }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerPanel(
    item: IListItemAll,
    properties: PlayerPropertyValue?,
    collapsible: Boolean = false,
    onMovieDetailsClick: (Int) -> Unit,
    onAlbumDetailsClick: (Int) -> Unit,
    viewModel: PlayerPanelViewModel = hiltViewModel(),
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val expandedMaxHeight = with(density) { 300.dp.toPx() }
    val collapsedMaxHeight = with(density) { 70.dp.toPx() }
    val movieId = remember(item) { item.id?.takeIf { item.type == IListItemBase.ItemType.Movie } }
    val albumId = remember(item) { item.albumid?.takeIf { item.type == IListItemBase.ItemType.Song } }

    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val elapsedTime = viewModel.playerElapsedTime.collectAsStateWithLifecycle()
    val progress = viewModel.playerProgress.collectAsStateWithLifecycle()
    val totalTime = viewModel.playerTotalTime.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val thumbnail by viewModel.playerThumbnail.collectAsStateWithLifecycle()

    var isCollapsed by rememberSaveable(collapsible) { mutableStateOf(collapsible) }
    val decayAnimationSpec = rememberSplineBasedDecay<Float>()
    val draggableState = remember {
        AnchoredDraggableState(
            initialValue = if (isCollapsed) PanelCollapseStatus.Collapsed else PanelCollapseStatus.Expanded,
            positionalThreshold = { it * 0.5f },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            snapAnimationSpec = TweenSpec(),
            decayAnimationSpec = decayAnimationSpec,
            confirmValueChange = { true },
            anchors = DraggableAnchors {
                PanelCollapseStatus.Expanded at 0f
                PanelCollapseStatus.Collapsed at expandedMaxHeight - collapsedMaxHeight
            }
        )
    }

    val expandProgress by remember {
        derivedStateOf { draggableState.progress(PanelCollapseStatus.Collapsed, PanelCollapseStatus.Expanded) }
    }
    val height by remember {
        derivedStateOf { with(density) { (expandedMaxHeight - draggableState.offset).toDp() } }
    }
    val columnPadding by remember { derivedStateOf { 10.dp * expandProgress } }
    val thumbnailHeight by remember { derivedStateOf { 70.dp + (30.dp * expandProgress) } }
    val expandedContentAlpha by remember { derivedStateOf { sqrt(expandProgress) } }

    LaunchedEffect(Unit) {
        snapshotFlow { draggableState.settledValue }.collect {
            if (it == PanelCollapseStatus.Collapsed) isCollapsed = true
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { draggableState.targetValue }.collect {
            if (it == PanelCollapseStatus.Expanded) isCollapsed = false
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = height)
            .then(
                if (collapsible) Modifier.anchoredDraggable(state = draggableState, orientation = Orientation.Vertical)
                else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f),
        ),
        shape = RectangleShape,
    ) {
        Column(modifier = Modifier.padding(columnPadding)) {
            Row(
                modifier = Modifier
                    .height(thumbnailHeight)
                    .fillMaxWidth()
                    .let {
                        if (movieId != null) it.clickable { onMovieDetailsClick(movieId) }
                        else if (albumId != null) it.clickable { onAlbumDetailsClick(albumId) }
                        else it
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                ) {
                    Surface(
                        shape = if (isCollapsed) RectangleShape else MaterialTheme.shapes.extraSmall,
                        color = Color.Transparent,
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
                }

                Column(
                    verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f)
                ) {
                    Text(
                        item.displayTitle,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    if (item.type == IListItemBase.ItemType.Movie) {
                        item.directorString?.also { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        if (!isCollapsed) item.year?.takeIf { it > 0 }?.toString()?.also { Text(it) }
                    } else if (item.type == IListItemBase.ItemType.Song) {
                        item.artistString?.also { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                        if (!isCollapsed) item.album?.takeIfNotBlank()
                            ?.also { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
                    }
                }

                if (isCollapsed) PlayPauseButton(isPlaying) { viewModel.playOrPause() }
            }

            if (!isCollapsed) {
                PlayerPanelPlaybackButtonRow(
                    properties = properties,
                    isPlaying = isPlaying,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(expandedContentAlpha),
                    onGotoNextClick = { viewModel.goToNextItem() },
                    onStopClick = { viewModel.stop() },
                    onPlayOrPauseClick = { viewModel.playOrPause() },
                    onFastRewindClick = { viewModel.decreasePlayerSpeed() },
                    onFastForwardClick = { viewModel.increasePlayerSpeed() },
                    onGotoPreviousClick = { viewModel.goToPreviousItem() },
                )
            }

            if (!isCollapsed) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    ToggleChip(
                        value = properties?.repeat?.let { it != PlayerRepeat.Off },
                        onClick = { viewModel.cycleRepeat() },
                        label = { Text(stringResource(R.string.repeat)) },
                        enabled = properties?.canrepeat == true,
                        leadingIcon = {
                            Icon(
                                if (properties?.repeat == PlayerRepeat.One) Icons.Sharp.RepeatOne else Icons.Sharp.Repeat,
                                null,
                            )
                        },
                    )
                    ToggleChip(
                        value = properties?.shuffled,
                        onClick = { viewModel.toggleShuffle() },
                        label = { Text(stringResource(R.string.shuffle)) },
                        enabled = properties?.canshuffle == true,
                        leadingIcon = { Icon(Icons.Sharp.Shuffle, null) },
                    )
                    AssistChip(
                        onClick = { viewModel.setFullscreen() },
                        leadingIcon = { Icon(Icons.Sharp.Fullscreen, null) },
                        label = { Text(stringResource(R.string.fullscreen)) },
                    )
                }

                PlayerPanelVolumeControlRow(
                    item = item,
                    properties = properties,
                    volume = volume,
                    isMuted = isMuted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(expandedContentAlpha),
                    onVolumeChange = { viewModel.setVolume(it) },
                    onIntermittentVolumeChange = { viewModel.setVolume(it, true) },
                    onIsMutedChange = { viewModel.setMute(it) },
                    onDisableSubtitleClick = { viewModel.disableSubtitle() },
                    onSetSubtitleClick = { viewModel.setSubtitle(it) },
                    onSubtitleSearchClick = { viewModel.openSubtitleSearch() },
                    onSetAudioStreamClick = { viewModel.setAudioStream(it) },
                )
                PlayerTimeSlider(
                    elapsedTime = elapsedTime,
                    progress = progress,
                    totalTime = totalTime,
                    enabled = properties?.canseek == true,
                    onSeekInProgress = { viewModel.seekInProgress(it) },
                    onSeekFinish = { viewModel.seekFinish(progress.value) },
                )
            }
        }
    }
}
