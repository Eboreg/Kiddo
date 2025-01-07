package us.huseli.kiddo.compose.screens.remotecontrol

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.Backspace
import androidx.compose.material.icons.sharp.CallToAction
import androidx.compose.material.icons.sharp.Circle
import androidx.compose.material.icons.sharp.Info
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import us.huseli.kiddo.R

@Composable
fun ControlPanel(
    buttonSize: Dp,
    baseIconSize: Dp = buttonSize * 0.4f,
    onKeyPress: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColors =
        IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    val secondaryColors =
        IconButtonDefaults.filledIconButtonColors(containerColor = Color.Transparent)
    val iconModifier = Modifier.size(baseIconSize)
    val arrowIconModifier = Modifier.size(baseIconSize * 1.4f)

    Box {
        Surface(
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            modifier = Modifier
                .size(buttonSize * 3)
                .align(Alignment.Center),
            content = {},
            shape = RoundedCornerShape(buttonSize / 10f),
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                ControlPanelButton(
                    colors = secondaryColors,
                    icon = {
                        Icon(Icons.Sharp.Menu, stringResource(R.string.context_menu), modifier = iconModifier)
                    },
                    onClick = { onKeyPress("ContextMenu") },
                    size = buttonSize,
                )
                ControlPanelButton(
                    colors = primaryColors,
                    icon = {
                        Icon(
                            Icons.Sharp.PlayArrow,
                            stringResource(R.string.up),
                            modifier = arrowIconModifier.rotate(-90f),
                        )
                    },
                    onClick = { onKeyPress("Up") },
                    size = buttonSize,
                )
                ControlPanelButton(
                    colors = secondaryColors,
                    icon = { Icon(Icons.Sharp.Info, stringResource(R.string.info), modifier = iconModifier) },
                    onClick = { onKeyPress("Info") },
                    size = buttonSize,
                )
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                ControlPanelButton(
                    colors = primaryColors,
                    icon = {
                        Icon(
                            Icons.Sharp.PlayArrow,
                            stringResource(R.string.left),
                            modifier = arrowIconModifier.rotate(180f),
                        )
                    },
                    onClick = { onKeyPress("Left") },
                    size = buttonSize,
                )
                ControlPanelButton(
                    colors = secondaryColors,
                    icon = {
                        Icon(
                            Icons.Sharp.Circle,
                            stringResource(R.string.select),
                            modifier = Modifier.size(baseIconSize * 0.75f),
                        )
                    },
                    onClick = { onKeyPress("Select") },
                    size = buttonSize,
                )
                ControlPanelButton(
                    colors = primaryColors,
                    icon = {
                        Icon(Icons.Sharp.PlayArrow, stringResource(R.string.right), modifier = arrowIconModifier)
                    },
                    onClick = { onKeyPress("Right") },
                    size = buttonSize,
                )
            }

            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                ControlPanelButton(
                    colors = secondaryColors,
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Sharp.Backspace,
                            stringResource(R.string.back),
                            modifier = iconModifier,
                        )
                    },
                    onClick = { onKeyPress("Back") },
                    size = buttonSize,
                )
                ControlPanelButton(
                    colors = primaryColors,
                    icon = {
                        Icon(
                            Icons.Sharp.PlayArrow,
                            stringResource(R.string.down),
                            modifier = arrowIconModifier.rotate(90f),
                        )
                    },
                    onClick = { onKeyPress("Down") },
                    size = buttonSize,
                )
                ControlPanelButton(
                    colors = secondaryColors,
                    icon = {
                        Icon(
                            Icons.Sharp.CallToAction,
                            stringResource(R.string.on_screen_display),
                            modifier = iconModifier,
                        )
                    },
                    onClick = { onKeyPress("ShowOSD") },
                    size = buttonSize,
                )
            }
        }
    }
}

@Composable
fun ControlPanelButton(
    colors: IconButtonColors,
    size: Dp,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledTonalIconButton(
        colors = colors,
        content = icon,
        onClick = onClick,
        modifier = modifier.size(size),
        shape = RoundedCornerShape(size / 10f),
    )
}
