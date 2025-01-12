package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Headphones
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import us.huseli.retaintheme.extensions.takeIfNotEmpty

@Composable
fun AudioStreamMenuButton(properties: PlayerPropertyValue?, onSetStreamClick: (Int) -> Unit) {
    var isMenuOpen by remember { mutableStateOf(false) }

    FilledIconButton(
        onClick = { isMenuOpen = true },
        content = { Icon(Icons.Sharp.Headphones, stringResource(R.string.audio_stream)) }
    )

    DropdownMenu(
        expanded = isMenuOpen,
        onDismissRequest = { isMenuOpen = false },
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.audio_stream), style = MaterialTheme.typography.titleMedium) },
            onClick = {},
            enabled = false,
            colors = MenuDefaults.itemColors(disabledTextColor = MaterialTheme.colorScheme.onSurface),
        )
        properties?.audiostreams?.takeIfNotEmpty()?.also { streams ->
            HorizontalDivider()
            for (stream in streams) {
                DropdownMenuItem(
                    text = { Text(stream.displayName) },
                    onClick = {
                        onSetStreamClick(stream.index)
                        isMenuOpen = false
                    },
                    modifier = Modifier.background(
                        if (stream.index == properties.currentaudiostream?.index)
                            MaterialTheme.colorScheme.primaryContainer
                        else Color.Unspecified,
                    ),
                )
            }
        }
    }
}
