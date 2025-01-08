package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.compose.screens.remotecontrol.ControlPanel
import us.huseli.kiddo.compose.PlayerPanel
import us.huseli.kiddo.compose.PlayerPanelNoMedia
import us.huseli.kiddo.viewmodels.RemoteControlViewModel

@Composable
fun RemoteControlScreen(
    onMovieDetailsClick: (Int) -> Unit,
    viewModel: RemoteControlViewModel = hiltViewModel()
) {
    val playerCoverImage by viewModel.playerCoverImage.collectAsStateWithLifecycle()
    val playerItemState by viewModel.playerItemUiState.collectAsStateWithLifecycle()
    val connectErrors by viewModel.connectErrors.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        playerCoverImage?.also {
            Image(it, null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop, alpha = 0.5f)
        }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            playerItemState?.also {
                PlayerPanel(state = it, onMovieDetailsClick = onMovieDetailsClick)
            } ?: run { PlayerPanelNoMedia(errors = connectErrors) }

            BoxWithConstraints(modifier = Modifier.padding(horizontal = 10.dp).padding(bottom = 10.dp)) {
                val maxSize = remember(this.maxWidth, this.maxHeight) { min(this.maxWidth, this.maxHeight) }

                ControlPanel(buttonSize = maxSize / 3f, onKeyPress = { viewModel.sendKeypress(it) })
            }
        }
    }
}
