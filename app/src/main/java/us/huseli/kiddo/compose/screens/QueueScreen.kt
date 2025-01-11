package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilledTonalIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.Logger
import us.huseli.kiddo.compose.screens.queue.QueueItemList
import us.huseli.kiddo.data.enums.PlaylistType
import us.huseli.kiddo.viewmodels.QueueViewModel

@Composable
fun QueueScreen(viewModel: QueueViewModel = hiltViewModel()) {
    val playlists by viewModel.playlists.collectAsStateWithLifecycle()
    var currentPlaylistType by remember { mutableStateOf<PlaylistType>(PlaylistType.Video) }
    val currentPlaylist = remember(currentPlaylistType, playlists) { playlists.find { it.type == currentPlaylistType } }
    val currentItem by viewModel.currentItem.collectAsStateWithLifecycle()

    Logger.log(
        "QueueScreen",
        "currentPlaylistType=$currentPlaylistType, currentPlaylist=$currentPlaylist, playlists=$playlists",
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            for (playlist in playlists) {
                FilledTonalIconToggleButton(
                    content = { Icon(playlist.type.icon, stringResource(playlist.type.resId)) },
                    checked = playlist.type == currentPlaylistType,
                    onCheckedChange = { if (it) currentPlaylistType = playlist.type },
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f),
                )
            }
        }

        currentPlaylist?.also { playlist ->
            QueueItemList(
                playlist = playlist,
                itemFlow = viewModel.flowPlaylistItems(playlist.playlistid),
                currentItemId = currentItem?.stringId,
                swapPositions = { from, to ->
                    viewModel.swapPlaylistPositions(
                        playlistId = playlist.playlistid,
                        from = from,
                        to = to,
                    )
                },
                onRemoveItemClick = { viewModel.removePlaylistItem(playlistId = playlist.playlistid, position = it) },
                onPlayItemClick = { viewModel.playPlaylistItem(playlistId = playlist.playlistid, position = it) },
                getItemThumbnailFlow = { viewModel.flowImageBitmap(it) },
            )
        }
    }
}
