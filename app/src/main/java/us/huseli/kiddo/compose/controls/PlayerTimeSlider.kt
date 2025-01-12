package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.sensibleFormat
import kotlin.time.Duration.Companion.seconds

@Composable
fun PlayerTimeSlider(
    elapsedTime: State<Int?>,
    progress: State<Float>,
    totalTime: State<Int?>,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onSeekInProgress: (Float) -> Unit,
    onSeekFinish: () -> Unit,
) {
    val elapsedTimeString =
        remember(elapsedTime.value) { elapsedTime.value?.seconds?.sensibleFormat() }
    val totalTimeString =
        remember(totalTime.value) { totalTime.value?.seconds?.sensibleFormat() }

    CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodySmall) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(elapsedTimeString ?: "", modifier = Modifier.width(50.dp), textAlign = TextAlign.Center)
            NiceSlider(
                value = progress.value,
                onValueChange = onSeekInProgress,
                onValueChangeFinished = onSeekFinish,
                modifier = Modifier.weight(1f),
                enabled = enabled && (totalTime.value ?: 0) > 0,
            )
            Text(totalTimeString ?: "", modifier = Modifier.width(50.dp), textAlign = TextAlign.Center)
        }
    }
}
