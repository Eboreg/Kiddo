package us.huseli.kiddo.compose

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrain
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R

@Composable
fun CollapsedText(
    text: String,
    maxLines: Int,
    style: TextStyle = LocalTextStyle.current,
    expandText: String = stringResource(R.string.show_more),
    showCollapseLink: Boolean = true,
    collapseText: String = stringResource(R.string.show_less),
    overflow: TextOverflow = TextOverflow.Ellipsis,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val maxHeightPx = with(density) { (style.lineHeight * maxLines).toPx().toInt() }
    var measuredMinHeightPx by remember { mutableIntStateOf(0) }
    val overflows = remember(maxHeightPx, measuredMinHeightPx) { measuredMinHeightPx > maxHeightPx }
    var expand by remember { mutableStateOf(false) }
    val height = remember(expand, maxHeightPx, measuredMinHeightPx) {
        with(density) { if (expand) measuredMinHeightPx.toDp() else maxHeightPx.toDp() }
    }
    val animatedHeight by animateDpAsState(height, tween(durationMillis = 100, easing = LinearEasing))

    Column(modifier = modifier) {
        Text(
            text = text,
            style = style,
            overflow = overflow,
            modifier = Modifier
                .then(if (overflows) Modifier.height(animatedHeight) else Modifier)
                // .height(animatedHeight)
                .layout { measurable, constraints ->
                    val newConstraints =
                        if (!expand) constraints.constrain(Constraints(maxHeight = maxHeightPx)) else constraints
                    val placeable = measurable.measure(constraints.constrain(newConstraints))

                    measuredMinHeightPx = measurable.minIntrinsicHeight(constraints.maxWidth)
                    layout(placeable.width, placeable.height) {
                        placeable.place(0, 0)
                    }
                },
        )

        if (overflows && (!expand || showCollapseLink)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                HorizontalDivider(modifier = Modifier.weight(0.1f))
                Surface(
                    onClick = { expand = !expand },
                    shape = MaterialTheme.shapes.extraSmall,
                    border = BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.outlineVariant),
                    content = {
                        Text(
                            text = if (!expand) expandText else collapseText,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(vertical = 5.dp, horizontal = 10.dp),
                        )
                    },
                )
                HorizontalDivider(modifier = Modifier.weight(0.1f))
            }
        }
    }
}
