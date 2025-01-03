package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.VolumeOff
import androidx.compose.material.icons.sharp.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.viewmodels.RemoteControlViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemoteControlScreen(
    modifier: Modifier = Modifier,
    viewModel: RemoteControlViewModel = hiltViewModel(),
) {
    val isMuted by viewModel.isMuted.collectAsStateWithLifecycle()
    val playerElapsedTime = viewModel.playerElapsedTimeString.collectAsStateWithLifecycle()
    val playerPercentage = viewModel.playerPercentage.collectAsStateWithLifecycle()
    val playerTotalTime = viewModel.playerTotalTimeString.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()

    var mutableVolume by rememberSaveable(volume) { mutableFloatStateOf(volume?.toFloat() ?: 0f) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = { viewModel.setFullScreen(true) },
                shape = MaterialTheme.shapes.extraSmall,
                content = { Text("Fullscreen on") },
            )
            OutlinedButton(
                onClick = { viewModel.setFullScreen(false) },
                shape = MaterialTheme.shapes.extraSmall,
                content = { Text("Fullscreen off") },
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Slider(
                value = mutableVolume,
                onValueChange = { mutableVolume = it },
                onValueChangeFinished = { viewModel.setVolume(mutableVolume.roundToInt()) },
                valueRange = 0f..100f,
                modifier = Modifier.weight(1f),
            )
            IconToggleButton(
                checked = isMuted,
                onCheckedChange = { viewModel.setMute(it) },
                content = { Icon(Icons.AutoMirrored.Sharp.VolumeOff, null) },
            )
        }

        BoxWithConstraints {
            val maxSize = remember(this.maxWidth, this.maxHeight) { min(this.maxWidth, this.maxHeight) }

            ControlPanel(
                buttonSize = maxSize / 3.5f,
                onOsdClick = { viewModel.sendKeypress("ShowOSD") },
                onUpClick = { viewModel.sendKeypress("Up") },
                onBackClick = { viewModel.sendKeypress("Back") },
                onDownClick = { viewModel.sendKeypress("Down") },
                onInfoClick = { viewModel.sendKeypress("Info") },
                onLeftClick = { viewModel.sendKeypress("Left") },
                onMenuClick = { viewModel.sendKeypress("ContextMenu") },
                onRightClick = { viewModel.sendKeypress("Right") },
                onSelectClick = { viewModel.sendKeypress("Select") },
            )
        }

        RoundIconButton(Icons.Sharp.Home) { viewModel.sendKeypress("Home") }

        PlayerTimeSlider(
            elapsedTime = playerElapsedTime,
            percentage = playerPercentage,
            totalTime = playerTotalTime,
            onValueChange = { viewModel.seekInProgress(it) },
            onValueChangeFinished = { viewModel.seekFinish(playerPercentage.value) },
        )
    }
}

@Composable
fun PlayerTimeSlider(
    elapsedTime: State<String?>,
    percentage: State<Float>,
    totalTime: State<String?>,
    modifier: Modifier = Modifier,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(elapsedTime.value ?: "")
        Slider(
            value = percentage.value,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = 0f..100f,
            modifier = Modifier.weight(1f),
        )
        Text(totalTime.value ?: "")
    }
}
