package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val host by viewModel.host.collectAsStateWithLifecycle()
    val jsonPort by viewModel.jsonPort.collectAsStateWithLifecycle()
    val websocketPort by viewModel.websocketPort.collectAsStateWithLifecycle()

    var mutableHost by rememberSaveable(host) { mutableStateOf(host ?: "") }
    var mutableJsonPort by rememberSaveable(jsonPort) { mutableStateOf(jsonPort.toString()) }
    var mutableWebsocketPort by rememberSaveable(websocketPort) { mutableStateOf(websocketPort.toString()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = mutableHost,
                onValueChange = { mutableHost = it },
                label = { Text("Host") },
            )
            OutlinedTextField(
                value = mutableJsonPort,
                onValueChange = { mutableJsonPort = it },
                label = { Text("Port") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = mutableWebsocketPort,
                onValueChange = { mutableWebsocketPort = it },
                label = { Text("Websocket port") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            )
            OutlinedButton(
                onClick = {
                    viewModel.setHost(mutableHost)
                    mutableJsonPort.toIntOrNull()?.also { viewModel.setJsonPort(it) }
                    mutableWebsocketPort.toIntOrNull()?.also { viewModel.setWebsocketPort(it) }
                },
                shape = MaterialTheme.shapes.extraSmall,
                content = { Text("Save") },
            )
        }
    }
}
