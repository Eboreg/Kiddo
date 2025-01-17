package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.managers.websocket.WebsocketManager
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.kiddo.routing.Routes

@Composable
fun PlayerPanelNoMedia(
    modifier: Modifier = Modifier,
    errors: List<String> = emptyList(),
    hostname: String?,
    status: WebsocketManager.Status,
    onNavigate: (Routes) -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.75f),
        ),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            val statusText = when (status) {
                WebsocketManager.Status.Pending -> stringResource(R.string.initializing_ellipsis)
                WebsocketManager.Status.Connecting -> hostname?.let {
                    stringResource(R.string.connecting_to_x, it)
                }

                WebsocketManager.Status.Connected -> hostname?.let {
                    stringResource(R.string.connected_to_x, it)
                }

                WebsocketManager.Status.NoHostname -> stringResource(R.string.no_hostname_configured)
                WebsocketManager.Status.Error -> null
            }

            statusText?.also {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (errors.isNotEmpty()) {
                for (error in errors) {
                    Text(error, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                }
            }

            if (status == WebsocketManager.Status.Connected) {
                Text(
                    text = stringResource(R.string.no_media_loaded),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else if (status == WebsocketManager.Status.NoHostname) {
                NiceOutlinedButton(
                    onClick = { onNavigate(Routes.Settings) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    text = { Text(stringResource(R.string.settings)) },
                )
            }
        }
    }
}
