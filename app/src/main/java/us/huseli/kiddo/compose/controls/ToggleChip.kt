package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToggleChip(
    value: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: @Composable (BoxScope.() -> Unit)? = null,
) {
    val colors = if (value) AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.75f),
        labelColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) else AssistChipDefaults.assistChipColors(
        labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
        leadingIconContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
    )

    AssistChip(
        onClick = onClick,
        label = label,
        modifier = modifier.height(36.dp),
        enabled = enabled,
        leadingIcon = leadingIcon?.let {
            {
                Box(
                    content = it,
                    modifier = Modifier
                        .height(20.dp)
                        .padding(start = 5.dp)
                )
            }
        },
        colors = colors,
    )
}
