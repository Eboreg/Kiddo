package us.huseli.kiddo.compose.controls

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import us.huseli.kiddo.R
import us.huseli.kiddo.data.notifications.data.InputOnInputRequested

@Composable
fun InputTextDialog(
    request: InputOnInputRequested,
    onDismissRequest: () -> Unit,
    onSend: (String) -> Unit,
) {
    var text by rememberSaveable(request.value) { mutableStateOf(request.value) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = { onSend(text) },
                content = { Text(stringResource(R.string.send)) },
            )
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest,
                content = { Text(stringResource(R.string.cancel)) },
            )
        },
        title = { Text(request.title) },
        text = {
            TextField(
                value = text,
                onValueChange = { text = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = when (request.type) {
                        InputOnInputRequested.Type.Password -> KeyboardType.Password
                        InputOnInputRequested.Type.NumericPassword -> KeyboardType.Password
                        InputOnInputRequested.Type.Number -> KeyboardType.Number
                        InputOnInputRequested.Type.Seconds -> KeyboardType.Number
                        else -> KeyboardType.Unspecified
                    },
                ),
                visualTransformation = when (request.type) {
                    InputOnInputRequested.Type.Password -> PasswordVisualTransformation()
                    InputOnInputRequested.Type.NumericPassword -> PasswordVisualTransformation()
                    else -> VisualTransformation.None
                },
            )
        },
        shape = MaterialTheme.shapes.small,
    )
}
