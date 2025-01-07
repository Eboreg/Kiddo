package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.VolumeOff
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import us.huseli.kiddo.R
import kotlin.math.roundToInt

@Composable
fun HorizontalVolumeControl(
    volume: Int?,
    isMuted: Boolean,
    onIsMutedChange: (Boolean) -> Unit,
    onVolumeChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var mutableVolume by rememberSaveable(volume) { mutableFloatStateOf(volume?.toFloat() ?: 0f) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NiceSlider(
            value = mutableVolume,
            onValueChange = { mutableVolume = it },
            onValueChangeFinished = { onVolumeChange(mutableVolume.roundToInt()) },
            modifier = Modifier.weight(1f),
            valueRange = 0f..100f,
            enabled = !isMuted,
        )
        FilledIconToggleButton(
            checked = isMuted,
            onCheckedChange = onIsMutedChange,
            content = { Icon(Icons.AutoMirrored.Sharp.VolumeOff, stringResource(R.string.toggle_mute)) },
        )
    }
}
