package us.huseli.kiddo.compose.screens.moviedetails

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R

@Composable
fun MovieDetailsPlot(plot: String, headlineStyle: TextStyle, modifier: Modifier = Modifier.Companion) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.plot),
            style = headlineStyle,
            modifier = Modifier.Companion.padding(bottom = 5.dp)
        )
        Text(plot)
    }
}
