package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NiceSlider(
    value: Float,
    enabled: Boolean = true,
    colors: SliderColors = SliderDefaults.colors(
        disabledThumbColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledActiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
        disabledActiveTickColor = MaterialTheme.colorScheme.primary,
    ),
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
) {
    Slider(
        value = value,
        enabled = enabled,
        colors = colors,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        modifier = modifier,
        valueRange = valueRange,
        thumb = {
            Spacer(
                modifier = Modifier
                    .size(20.dp)
                    .background(
                        color = if (enabled) colors.thumbColor else colors.disabledThumbColor,
                        shape = CircleShape,
                    )
            )
        },
        track = { sliderState ->
            SliderDefaults.Track(
                sliderState = sliderState,
                thumbTrackGapSize = 0.dp,
                drawStopIndicator = {},
                modifier = Modifier.height(5.dp),
                colors = colors,
                enabled = enabled,
            )
        },
    )
}
