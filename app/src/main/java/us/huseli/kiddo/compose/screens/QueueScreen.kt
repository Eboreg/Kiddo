package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sh.calvin.reorderable.rememberReorderableLazyListState
import us.huseli.kiddo.compose.screens.queue.QueueItem
import us.huseli.kiddo.data.PlaylistWithItems
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.viewmodels.QueueViewModel

@Composable
fun QueueScreen(
    modifier: Modifier = Modifier,
    viewModel: QueueViewModel = hiltViewModel(),
) {
    var playlistType by remember { mutableStateOf<PlaylistType>(PlaylistType.Video) }

    val currentItem by viewModel.currentItem.collectAsStateWithLifecycle()
    var playlistCombos by remember { mutableStateOf<Map<PlaylistType, PlaylistWithItems>>(emptyMap()) }
    val currentPlaylistCombo =
        remember<PlaylistWithItems?>(playlistCombos, playlistType) { playlistCombos[playlistType] }
    var playlistItems by remember(currentPlaylistCombo) { mutableStateOf(currentPlaylistCombo?.items ?: emptyList()) }
    val isReorderable = remember(currentPlaylistCombo) { currentPlaylistCombo?.playlist?.type != PlaylistType.Picture }
    val listState = rememberLazyListState()

    val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            playlistItems = playlistItems.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            currentPlaylistCombo?.playlist?.playlistId?.also {
                viewModel.swapPlaylistPositions(
                    playlistId = it,
                    position1 = from.index,
                    position2 = to.index,
                )
            }
        }
    )

    LaunchedEffect(reorderableState.isAnyItemDragging) {
        if (!reorderableState.isAnyItemDragging) {
            currentPlaylistCombo?.also { combo ->
                playlistCombos += combo.playlist.type to combo.copy(items = viewModel.getPlaylistItems(combo.playlist.playlistid))
            }
            viewModel.playlists.collect { combos ->
                playlistCombos = combos
            }
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            playlistCombos.values.forEach { combo ->
                FilledTonalIconToggleButton(
                    content = { Icon(combo.playlist.type.icon, stringResource(combo.playlist.type.resId)) },
                    checked = combo.playlist.type == playlistType,
                    onCheckedChange = { if (it) playlistType = combo.playlist.type },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        LazyColumn(state = listState, modifier = Modifier.fillMaxHeight()) {
            itemsIndexed(playlistItems, key = { index, entry -> entry.stringIdForced }) { index, entry ->
                QueueItem(
                    entry = entry,
                    index = index,
                    isReorderable = isReorderable,
                    reorderableState = reorderableState,
                    playlistType = playlistType,
                    currentItemId = currentItem?.stringId,
                    onRemoveClick = {
                        currentPlaylistCombo?.playlist?.playlistId?.also {
                            viewModel.removePlaylistItem(it, index)
                        }
                    },
                    onPlayClick = {
                        currentPlaylistCombo?.playlist?.playlistId?.also { viewModel.playPlaylistItem(it, index) }
                    },
                    getThumbnail = { entry.thumbnail?.let { viewModel.getImageBitmap(it) } },
                )
            }
        }
    }
}
