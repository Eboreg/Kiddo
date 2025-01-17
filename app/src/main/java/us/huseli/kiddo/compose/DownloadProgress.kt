package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.controls.NiceLinearProgressIndicator
import us.huseli.kiddo.managers.download.DownloadProgress

@Composable
fun DownloadProgress(progress: DownloadProgress, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = progress.notificationText ?: stringResource(R.string.downloading_ellipsis),
            style = MaterialTheme.typography.bodySmall
        )
        progress.percent?.also {
            NiceLinearProgressIndicator(progress = { it.toFloat() / 100 })
        }
    }
}
