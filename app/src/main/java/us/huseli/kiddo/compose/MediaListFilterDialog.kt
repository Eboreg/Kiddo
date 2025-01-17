package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.retaintheme.extensions.takeIfNotBlank

data class MediaListFilterParameter(
    val key: String,
    val value: String?,
    val label: String,
)

fun List<MediaListFilterParameter>.clear() = map { it.copy(value = null) }

fun List<MediaListFilterParameter>.getValue(key: String) = find { it.key == key }?.value

@Composable
fun MediaListFilterDialog(
    params: List<MediaListFilterParameter>,
    onSubmit: (List<MediaListFilterParameter>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var data by remember(params) {
        mutableStateOf<Map<String, String>>(
            params.associate { param ->
                param.key to (param.value ?: "")
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            NiceOutlinedButton(
                onClick = {
                    onSubmit(
                        params.map { param ->
                            param.copy(value = data[param.key]?.takeIfNotBlank())
                        }
                    )
                },
                text = { Text(stringResource(R.string.apply)) },
            )
        },
        dismissButton = {
            TextButton(onClick = { onSubmit(params.clear()) }) { Text(stringResource(R.string.clear_all)) }
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) }
        },
        title = { Text(stringResource(R.string.filters)) },
        shape = MaterialTheme.shapes.small,
        text = {
            Column {
                for (param in params) {
                    OutlinedTextField(
                        value = data[param.key]!!,
                        onValueChange = { data += param.key to it },
                        label = { Text(param.label) },
                        singleLine = true,
                    )
                }
            }
        },
    )
}
