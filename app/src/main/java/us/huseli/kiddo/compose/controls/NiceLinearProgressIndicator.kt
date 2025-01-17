package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NiceLinearProgressIndicator(
    progress: () -> Float,
    modifier: Modifier = Modifier,
    trackColor: Color = MaterialTheme.colorScheme.primaryContainer,
    color: Color = MaterialTheme.colorScheme.primary,
    gapSize: Dp = 0.dp,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
    drawStopIndicator: DrawScope.() -> Unit = {},
) {
    LinearProgressIndicator(
        progress = progress,
        drawStopIndicator = drawStopIndicator,
        trackColor = trackColor,
        color = color,
        gapSize = gapSize,
        strokeCap = strokeCap,
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
    )
}
