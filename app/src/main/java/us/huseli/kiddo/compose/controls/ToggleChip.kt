package us.huseli.kiddo.compose.controls

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ToggleChip(
    value: Boolean?,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    val colors = when (value) {
        true -> AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f),
            labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
        )

        false -> AssistChipDefaults.assistChipColors(
            labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            leadingIconContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )

        null -> AssistChipDefaults.assistChipColors(
            labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            leadingIconContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        )
    }
    val border = when (value) {
        false -> AssistChipDefaults.assistChipBorder(
            enabled,
            borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )

        else -> AssistChipDefaults.assistChipBorder(enabled)
    }

    AssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        colors = colors,
        border = border,
    )
}
