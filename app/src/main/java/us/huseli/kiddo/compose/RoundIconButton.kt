package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun RoundIconButton(imageVector: ImageVector, onClick: () -> Unit) {
    IconButton(
        colors = IconButtonDefaults.iconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        onClick = onClick,
        content = { Icon(imageVector, null) },
        modifier = Modifier.size(50.dp),
    )
}
