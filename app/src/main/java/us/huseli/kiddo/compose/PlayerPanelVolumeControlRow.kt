package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Fullscreen
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.AudioStreamMenuButton
import us.huseli.kiddo.compose.controls.HorizontalVolumeControl
import us.huseli.kiddo.compose.controls.SubtitleMenuButton
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.kiddo.data.types.interfaces.IListItemAll
import us.huseli.kiddo.data.types.interfaces.IListItemBase

@Composable
fun PlayerPanelVolumeControlRow(
    item: IListItemAll,
    properties: PlayerPropertyValue?,
    volume: Int?,
    isMuted: Boolean,
    modifier: Modifier = Modifier,
    onVolumeChange: (Int) -> Unit,
    onIntermittentVolumeChange: (Int) -> Unit,
    onIsMutedChange: (Boolean) -> Unit,
    onDisableSubtitleClick: () -> Unit,
    onSetSubtitleClick: (Int) -> Unit,
    onSubtitleSearchClick: () -> Unit,
    onSetAudioStreamClick: (Int) -> Unit
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        HorizontalVolumeControl(
            volume = volume,
            isMuted = isMuted,
            onVolumeChange = onVolumeChange,
            onIntermittentVolumeChange = onIntermittentVolumeChange,
            onIsMutedChange = onIsMutedChange,
            modifier = Modifier.weight(1f),
        )
        if (item.type != IListItemBase.ItemType.Song && item.type != IListItemBase.ItemType.Picture) {
            Row {
                SubtitleMenuButton(
                    properties = properties,
                    onDisableSubtitleClick = onDisableSubtitleClick,
                    onSetSubtitleClick = onSetSubtitleClick,
                    onSubtitleSearchClick = onSubtitleSearchClick,
                )
                AudioStreamMenuButton(
                    properties = properties,
                    onSetStreamClick = onSetAudioStreamClick,
                )
            }
        }
    }
}
