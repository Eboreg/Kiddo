package us.huseli.kiddo.compose.screens.queue

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import sh.calvin.reorderable.rememberReorderableLazyListState
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.requests.PlaylistGetPlaylists
import us.huseli.kiddo.data.types.interfaces.IListItemAll

@Composable
fun QueueItemList(
    playlist: PlaylistGetPlaylists.ResultItem,
    itemFlow: Flow<List<IListItemAll>>,
    currentItemId: String?,
    swapPositions: (Int?, Int?) -> Unit,
    onRemoveItemClick: (Int) -> Unit,
    onPlayItemClick: (Int) -> Unit,
    getItemThumbnailFlow: (String?) -> StateFlow<ImageBitmap?>,
) {
    val listState = rememberLazyListState()
    var items by remember(playlist) { mutableStateOf<List<IListItemAll>>(emptyList()) }
    val isReorderable = remember(playlist) { playlist.type != PlaylistType.Picture }
    val reorderableState = rememberReorderableLazyListState(
        lazyListState = listState,
        onMove = { from, to ->
            items = items.toMutableList().apply {
                add(to.index, removeAt(from.index))
            }
            swapPositions(from.index, to.index)
        }
    )

    LaunchedEffect(playlist) {
        itemFlow.collect {
            if (!reorderableState.isAnyItemDragging) {
                items = it
            }
        }
    }

    LazyColumn(state = listState, modifier = Modifier.fillMaxHeight()) {
        itemsIndexed(items, key = { index, entry -> entry.stringIdForced }) { index, entry ->
            val thumbnail by getItemThumbnailFlow(entry.thumbnail).collectAsStateWithLifecycle()

            QueueItem(
                entry = entry,
                index = index,
                isReorderable = isReorderable,
                reorderableState = reorderableState,
                playlistType = playlist.type,
                currentItemId = currentItemId,
                onRemoveClick = { onRemoveItemClick(index) },
                onPlayClick = { onPlayItemClick(index) },
                thumbnail = thumbnail,
            )
        }
    }
}
