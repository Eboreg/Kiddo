package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import us.huseli.kiddo.R

@Composable
fun LoadingFlash(
    paddingTop: Dp = 60.dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .zIndex(1f)
            .fillMaxWidth()
            .padding(top = paddingTop)
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraSmall,
            tonalElevation = 5.dp,
            border = ButtonDefaults.outlinedButtonBorder(true),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Text(
                stringResource(R.string.loading_ellipsis),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}
