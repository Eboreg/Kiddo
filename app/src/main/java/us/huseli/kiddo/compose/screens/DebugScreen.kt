package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import us.huseli.retaintheme.ui.theme.LocalBasicColors

@Composable
fun DebugScreen() {
    val density = LocalDensity.current

    Column(
        modifier = Modifier
            .padding(10.dp)
            .verticalScroll(state = rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("1 dp = ${with(density) { 1.dp.toPx() }} px")
        Text("1 px = ${with(density) { 1.toDp() }}")

        Column {
            val colorSamples = mapOf(
                "background" to MaterialTheme.colorScheme.background,
                "error" to MaterialTheme.colorScheme.error,
                "errorContainer" to MaterialTheme.colorScheme.errorContainer,
                "inverseOnSurface" to MaterialTheme.colorScheme.inverseOnSurface,
                "inversePrimary" to MaterialTheme.colorScheme.inversePrimary,
                "inverseSurface" to MaterialTheme.colorScheme.inverseSurface,
                "onBackground" to MaterialTheme.colorScheme.onBackground,
                "onError" to MaterialTheme.colorScheme.onError,
                "onErrorContainer" to MaterialTheme.colorScheme.onErrorContainer,
                "onPrimary" to MaterialTheme.colorScheme.onPrimary,
                "onPrimaryContainer" to MaterialTheme.colorScheme.onPrimaryContainer,
                "onSecondary" to MaterialTheme.colorScheme.onSecondary,
                "onSecondaryContainer" to MaterialTheme.colorScheme.onSecondaryContainer,
                "onSurface" to MaterialTheme.colorScheme.onSurface,
                "onSurfaceVariant" to MaterialTheme.colorScheme.onSurfaceVariant,
                "onTertiary" to MaterialTheme.colorScheme.onTertiary,
                "onTertiaryContainer" to MaterialTheme.colorScheme.onTertiaryContainer,
                "outline" to MaterialTheme.colorScheme.outline,
                "outlineVariant" to MaterialTheme.colorScheme.outlineVariant,
                "primary" to MaterialTheme.colorScheme.primary,
                "primaryContainer" to MaterialTheme.colorScheme.primaryContainer,
                "scrim" to MaterialTheme.colorScheme.scrim,
                "secondary" to MaterialTheme.colorScheme.secondary,
                "secondaryContainer" to MaterialTheme.colorScheme.secondaryContainer,
                "surface" to MaterialTheme.colorScheme.surface,
                "surfaceBright" to MaterialTheme.colorScheme.surfaceBright,
                "surfaceContainer" to MaterialTheme.colorScheme.surfaceContainer,
                "surfaceContainerHigh" to MaterialTheme.colorScheme.surfaceContainerHigh,
                "surfaceContainerHighest" to MaterialTheme.colorScheme.surfaceContainerHighest,
                "surfaceContainerLow" to MaterialTheme.colorScheme.surfaceContainerLow,
                "surfaceContainerLowest" to MaterialTheme.colorScheme.surfaceContainerLowest,
                "surfaceDim" to MaterialTheme.colorScheme.surfaceDim,
                "surfaceTint" to MaterialTheme.colorScheme.surfaceTint,
                "surfaceVariant" to MaterialTheme.colorScheme.surfaceVariant,
                "tertiary" to MaterialTheme.colorScheme.tertiary,
                "tertiaryContainer" to MaterialTheme.colorScheme.tertiaryContainer,
                "Blue" to LocalBasicColors.current.Blue,
                "Brown" to LocalBasicColors.current.Brown,
                "Gray" to LocalBasicColors.current.Gray,
                "Green" to LocalBasicColors.current.Green,
                "Orange" to LocalBasicColors.current.Orange,
                "Cerulean" to LocalBasicColors.current.Cerulean,
                "Pink" to LocalBasicColors.current.Pink,
                "Purple" to LocalBasicColors.current.Purple,
                "Red" to LocalBasicColors.current.Red,
                "Teal" to LocalBasicColors.current.Teal,
                "Yellow" to LocalBasicColors.current.Yellow,
            )

            ColorSamples(samples = colorSamples)
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            var sampleText by rememberSaveable { mutableStateOf("Hora") }

            OutlinedTextField(
                value = sampleText,
                onValueChange = { sampleText = it },
                placeholder = { Text("Sample text") },
                singleLine = true,
                supportingText = { Text("Sample text") },
            )

            Text("MaterialTheme.typography:", style = MaterialTheme.typography.titleMedium)

            FontSample(name = "displayLarge", style = MaterialTheme.typography.displayLarge, sample = sampleText)
            FontSample(name = "displayMedium", style = MaterialTheme.typography.displayMedium, sample = sampleText)
            FontSample(name = "displaySmall", style = MaterialTheme.typography.displaySmall, sample = sampleText)
            FontSample(name = "headlineLarge", style = MaterialTheme.typography.headlineLarge, sample = sampleText)
            FontSample(
                name = "headlineMedium",
                style = MaterialTheme.typography.headlineMedium,
                sample = sampleText,
            )
            FontSample(name = "headlineSmall", style = MaterialTheme.typography.headlineSmall, sample = sampleText)
            FontSample(name = "titleLarge", style = MaterialTheme.typography.titleLarge, sample = sampleText)
            FontSample(name = "titleMedium", style = MaterialTheme.typography.titleMedium, sample = sampleText)
            FontSample(name = "titleSmall", style = MaterialTheme.typography.titleSmall, sample = sampleText)
            FontSample(name = "bodyLarge", style = MaterialTheme.typography.bodyLarge, sample = sampleText)
            FontSample(name = "bodyMedium", style = MaterialTheme.typography.bodyMedium, sample = sampleText)
            FontSample(name = "bodySmall", style = MaterialTheme.typography.bodySmall, sample = sampleText)
            FontSample(name = "labelLarge", style = MaterialTheme.typography.labelLarge, sample = sampleText)
            FontSample(name = "labelMedium", style = MaterialTheme.typography.labelMedium, sample = sampleText)
            FontSample(name = "labelSmall", style = MaterialTheme.typography.labelSmall, sample = sampleText)
            FontSample(style = null, name = "Default text", sample = sampleText)
        }
    }
}

@Composable
fun FontSample(style: TextStyle?, name: String, sample: String) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    Column {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded },
        ) {
            Text(name)
            if (style != null) Text(sample, style = style)
            else Text(sample)
        }

        if (isExpanded && style != null) {
            Text(
                "fontSize=${style.fontSize}, fontWeight=${style.fontWeight}, letterSpacing=${style.letterSpacing}, " +
                    "lineHeight=${style.lineHeight}",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun ColorSamples(samples: Map<String, Color>) {
    for (chunk in samples.toList().chunked(2)) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            for (sample in chunk) {
                ColorSample(name = sample.first, color = sample.second)
            }
        }
    }
}

@Composable
fun RowScope.ColorSample(name: String, color: Color, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.weight(0.5f),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .border(1.dp, Color.Black)
                .background(color)
        )
        Text(name, style = MaterialTheme.typography.labelSmall)
    }
}
