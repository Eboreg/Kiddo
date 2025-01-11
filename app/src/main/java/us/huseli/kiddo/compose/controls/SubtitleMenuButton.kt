package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Subtitles
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import us.huseli.kiddo.R
import us.huseli.kiddo.data.types.PlayerPropertyValue

@Composable
fun SubtitleMenuButton(
    properties: PlayerPropertyValue?,
    onDisableSubtitleClick: () -> Unit,
    onSetSubtitleClick: (Int) -> Unit,
    onSubtitleSearchClick: () -> Unit,
) {
    var isMenuOpen by remember { mutableStateOf(false) }

    FilledIconButton(
        onClick = { isMenuOpen = true },
        colors = if (properties?.subtitleenabled == true) IconButtonDefaults.filledIconButtonColors()
        else IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        content = { Icon(Icons.Sharp.Subtitles, stringResource(R.string.subtitles)) },
    )

    DropdownMenu(
        expanded = isMenuOpen,
        onDismissRequest = { isMenuOpen = false },
    ) {
        DropdownMenuItem(
            text = {
                Text(stringResource(R.string.subtitles), style = MaterialTheme.typography.headlineSmall)
            },
            onClick = {},
            enabled = false,
            colors = MenuDefaults.itemColors(disabledTextColor = MaterialTheme.colorScheme.onSurface),
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.search_ellipsis)) },
            onClick = {
                onSubtitleSearchClick()
                isMenuOpen = false
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.none)) },
            onClick = {
                onDisableSubtitleClick()
                isMenuOpen = false
            },
            modifier = Modifier.background(
                if (properties?.subtitleenabled == false) MaterialTheme.colorScheme.primaryContainer
                else Color.Unspecified,
            ),
        )
        properties?.subtitles?.forEach { subtitle ->
            DropdownMenuItem(
                text = { Text(subtitle.displayName) },
                onClick = {
                    onSetSubtitleClick(subtitle.index)
                    isMenuOpen = false
                },
                modifier = Modifier.background(
                    if (subtitle.index == properties.currentsubtitle?.index && properties.subtitleenabled == true)
                        MaterialTheme.colorScheme.primaryContainer
                    else Color.Unspecified,
                ),
            )
        }
    }
}
