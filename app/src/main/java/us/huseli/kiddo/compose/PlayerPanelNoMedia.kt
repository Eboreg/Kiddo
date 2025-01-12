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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.KodiWebsocketEngine
import us.huseli.kiddo.R

@Composable
fun PlayerPanelNoMedia(
    modifier: Modifier = Modifier,
    errors: List<String> = emptyList(),
    hostname: String?,
    status: KodiWebsocketEngine.Status,
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
                KodiWebsocketEngine.Status.Pending -> stringResource(R.string.initializing_ellipsis)
                KodiWebsocketEngine.Status.Connecting -> hostname?.let {
                    stringResource(R.string.connecting_to_x, it)
                }

                KodiWebsocketEngine.Status.Connected -> hostname?.let {
                    stringResource(R.string.connected_to_x, it)
                }

                KodiWebsocketEngine.Status.NoHostname -> stringResource(R.string.no_hostname_configured)
                KodiWebsocketEngine.Status.Error -> null
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

            if (status == KodiWebsocketEngine.Status.Connected) {
                Text(
                    text = stringResource(R.string.no_media_loaded),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
