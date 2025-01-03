package us.huseli.kiddo.compose

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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ControlPanel(
    buttonSize: Dp,
    baseIconSize: Dp = buttonSize * 0.4f,
    onOsdClick: () -> Unit,
    onUpClick: () -> Unit,
    onInfoClick: () -> Unit,
    onLeftClick: () -> Unit,
    onSelectClick: () -> Unit,
    onRightClick: () -> Unit,
    onBackClick: () -> Unit,
    onDownClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primaryColors =
        IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    val secondaryColors =
        IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.background)
    val cornerRadius = buttonSize / 10f
    val shape = RoundedCornerShape(cornerRadius)
    val iconModifier = Modifier.size(baseIconSize)
    val arrowIconModifier = Modifier.size(baseIconSize * 1.4f)
    val buttonModifier = Modifier.size(buttonSize)

    Box {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier
                .size((buttonSize * 3) - (cornerRadius * 2))
                .align(Alignment.Center),
            content = {},
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                FilledTonalIconButton(
                    colors = secondaryColors,
                    content = { Icon(Icons.Sharp.Menu, null, modifier = iconModifier) },
                    onClick = onMenuClick,
                    shape = shape,
                    modifier = buttonModifier,
                )
                FilledTonalIconButton(
                    colors = primaryColors,
                    content = { Icon(Icons.Sharp.PlayArrow, null, modifier = arrowIconModifier.rotate(-90f)) },
                    onClick = onUpClick,
                    shape = RoundedCornerShape(cornerRadius, cornerRadius),
                    modifier = buttonModifier,
                )
                FilledTonalIconButton(
                    colors = secondaryColors,
                    content = { Icon(Icons.Sharp.Info, null, modifier = iconModifier) },
                    onClick = onInfoClick,
                    shape = shape,
                    modifier = buttonModifier,
                )
            }

            Row(horizontalArrangement = Arrangement.Center) {
                FilledTonalIconButton(
                    colors = primaryColors,
                    content = { Icon(Icons.Sharp.PlayArrow, null, modifier = arrowIconModifier.rotate(180f)) },
                    onClick = onLeftClick,
                    shape = RoundedCornerShape(cornerRadius, 0.dp, 0.dp, cornerRadius),
                    modifier = buttonModifier,
                )
                FilledTonalIconButton(
                    colors = primaryColors,
                    content = { Icon(Icons.Sharp.Circle, null, modifier = Modifier.size(baseIconSize * 0.75f)) },
                    onClick = onSelectClick,
                    shape = RectangleShape,
                    modifier = buttonModifier,
                )
                FilledTonalIconButton(
                    colors = primaryColors,
                    content = { Icon(Icons.Sharp.PlayArrow, null, modifier = arrowIconModifier) },
                    onClick = onRightClick,
                    shape = RoundedCornerShape(0.dp, cornerRadius, cornerRadius),
                    modifier = buttonModifier,
                )
            }

            Row(horizontalArrangement = Arrangement.Center) {
                FilledTonalIconButton(
                    content = { Icon(Icons.AutoMirrored.Sharp.Backspace, null, modifier = iconModifier) },
                    onClick = onBackClick,
                    shape = shape,
                    colors = secondaryColors,
                    modifier = buttonModifier,
                )
                FilledTonalIconButton(
                    onClick = onDownClick,
                    shape = RoundedCornerShape(0.dp, 0.dp, cornerRadius, cornerRadius),
                    content = { Icon(Icons.Sharp.PlayArrow, null, modifier = arrowIconModifier.rotate(90f)) },
                    colors = primaryColors,
                    modifier = buttonModifier,
                )
                FilledTonalIconButton(
                    onClick = onOsdClick,
                    content = { Icon(Icons.Sharp.CallToAction, null, modifier = iconModifier) },
                    shape = shape,
                    colors = secondaryColors,
                    modifier = buttonModifier,
                )
            }
        }
    }
}
