package us.huseli.kiddo.compose.screens.queue

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import us.huseli.kiddo.R
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.data.types.ListItemAll
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.takeIfNotEmpty
import us.huseli.retaintheme.extensions.sensibleFormat
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@Composable
fun LazyItemScope.QueueItem(
    entry: ListItemAll,
    index: Int,
    isReorderable: Boolean,
    reorderableState: ReorderableLazyListState,
    playlistType: PlaylistType,
    currentItemId: String?,
    onRemoveClick: () -> Unit,
    onPlayClick: () -> Unit,
    getThumbnail: suspend () -> ImageBitmap?,
) {
    val isPlaying = remember(entry.stringId, currentItemId) {
        currentItemId != null && currentItemId == entry.stringId
    }
    var thumbnail by remember { mutableStateOf<ImageBitmap?>(null) }
    var isDropdownOpen by rememberSaveable(entry) { mutableStateOf(false) }

    LaunchedEffect(entry.thumbnail) {
        thumbnail = getThumbnail()
    }

    ReorderableItem(
        enabled = isReorderable,
        state = reorderableState,
        key = entry.stringIdForced,
    ) { isDragging ->
        ListItem(
            tonalElevation = if (isDragging) 0.dp else 10.dp * (index % 2),
            colors = if (isDragging)
                ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            else ListItemDefaults.colors(),
            shadowElevation = if (isDragging) 20.dp else 0.dp,
            headlineContent = {
                Text(
                    text = entry.title?.takeIfNotBlank() ?: entry.label,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            supportingContent = {
                val text = listOfNotNull(
                    entry.artistString,
                    entry.directorString,
                    entry.runtime?.toDuration(DurationUnit.SECONDS)?.sensibleFormat(),
                ).takeIfNotEmpty()?.joinToString(" · ")

                text?.let { Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis) }
            },
            leadingContent = {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .longPressDraggableHandle(enabled = isReorderable)
                ) {
                    thumbnail?.also {
                        Image(
                            bitmap = it,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } ?: run { Icon(playlistType.icon, null, modifier = Modifier.fillMaxSize()) }
                }
            },
            trailingContent = {
                IconButton(
                    onClick = { isDropdownOpen = true },
                    content = { Icon(Icons.Sharp.MoreVert, null) },
                )
                DropdownMenu(
                    expanded = isDropdownOpen,
                    onDismissRequest = { isDropdownOpen = false },
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.remove)) },
                        onClick = onRemoveClick,
                    )
                }
            },
            modifier = Modifier
                .clickable(onClick = onPlayClick)
                .then(
                    if (isPlaying) Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    ) else Modifier
                ),
        )
    }
}
