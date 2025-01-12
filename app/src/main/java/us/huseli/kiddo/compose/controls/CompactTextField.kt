package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CompactTextField(
    value: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(color = color),
    placeholder: @Composable (() -> Unit)? = null,
    label: @Composable (() -> Unit)? = null,
    showClearIcon: Boolean = true,
    minHeight: Dp = 40.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    focusRequester: FocusRequester = remember { FocusRequester() },
    onValueChange: (String) -> Unit = {},
    onFocusChange: (FocusState) -> Unit = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        value = value,
        interactionSource = interactionSource,
        visualTransformation = visualTransformation,
        onValueChange = onValueChange,
        modifier = modifier
            .heightIn(min = minHeight)
            .height(IntrinsicSize.Min)
            .padding(0.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                onFocusChange(it)
            }
            .focusRequester(focusRequester),
        singleLine = true,
        enabled = enabled,
        textStyle = textStyle,
        cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            Column(verticalArrangement = Arrangement.SpaceBetween) {
                Row(modifier = Modifier.height(20.dp)) {
                    label?.also {
                        CompositionLocalProvider(
                            LocalTextStyle provides textStyle.copy(
                                fontSize = textStyle.fontSize.times(0.85f),
                                color = color.copy(alpha = 0.75f)
                            ),
                            content = label,
                        )
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = 22.dp)
                                .align(Alignment.CenterVertically)
                        ) {
                            innerTextField()
                            if (!isFocused && value.isEmpty()) {
                                placeholder?.also {
                                    CompositionLocalProvider(
                                        LocalTextStyle provides textStyle.copy(
                                            color = color.copy(alpha = 0.5f)
                                        ),
                                        content = placeholder,
                                    )
                                }
                            }
                        }
                        if (value.isNotEmpty() && showClearIcon) {
                            Icon(
                                imageVector = Icons.Sharp.Clear,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier
                                    .requiredSize(22.dp)
                                    .clickable {
                                        onValueChange("")
                                    }
                            )
                        }
                    }
                    HorizontalDivider(
                        color = if (isFocused) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
    )
}
