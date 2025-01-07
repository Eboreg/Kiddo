package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.CompactTextField
import us.huseli.kiddo.viewmodels.SettingsViewModel

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val hostname by viewModel.hostname.collectAsStateWithLifecycle()
    val jsonPort by viewModel.jsonPort.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val username by viewModel.username.collectAsStateWithLifecycle()
    val websocketPort by viewModel.websocketPort.collectAsStateWithLifecycle()

    var mutableHostname by rememberSaveable(hostname) { mutableStateOf(hostname ?: "") }
    var mutableJsonPort by rememberSaveable(jsonPort) { mutableStateOf(jsonPort.toString()) }
    var mutableWebsocketPort by rememberSaveable(websocketPort) { mutableStateOf(websocketPort.toString()) }
    var mutableUsername by rememberSaveable { mutableStateOf(username ?: "") }
    var mutablePassword by rememberSaveable { mutableStateOf(password ?: "") }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CompactTextField(
                value = mutableHostname,
                onValueChange = { mutableHostname = it },
                label = { Text(stringResource(R.string.host)) },
                modifier = Modifier.weight(0.7f),
            )
            CompactTextField(
                value = mutableJsonPort,
                onValueChange = { mutableJsonPort = it },
                label = { Text(stringResource(R.string.port)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.3f),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            CompactTextField(
                value = mutableUsername,
                onValueChange = { mutableUsername = it },
                label = { Text(stringResource(R.string.username)) },
                modifier = Modifier.weight(0.7f),
            )
            CompactTextField(
                value = mutableWebsocketPort,
                onValueChange = { mutableWebsocketPort = it },
                label = { Text(stringResource(R.string.websocket_port)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.3f),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            CompactTextField(
                value = mutablePassword,
                onValueChange = { mutablePassword = it },
                label = { Text(stringResource(R.string.password)) },
                modifier = Modifier.weight(0.7f),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
            )
            OutlinedButton(
                onClick = {
                    viewModel.update(
                        hostname = mutableHostname,
                        username = mutableUsername,
                        password = mutablePassword,
                        jsonPort = mutableJsonPort,
                        websocketPort = mutableWebsocketPort,
                    )
                },
                shape = MaterialTheme.shapes.extraSmall,
                content = { Text(stringResource(R.string.save)) },
                modifier = Modifier.weight(0.3f),
            )
        }
    }
}
