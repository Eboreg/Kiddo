package us.huseli.kiddo.compose.screens.moviedetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.sensibleFormat
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.takeIfNotEmpty
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailsInfo(
    details: VideoDetailsMovie,
    headlineStyle: TextStyle,
    onPersonClick: (String) -> Unit,
    onGenreClick: (String) -> Unit,
    onYearClick: (Int) -> Unit,
    onCountryClick: (String) -> Unit,
    modifier: Modifier = Modifier.Companion,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(stringResource(R.string.information), style = headlineStyle)

        details.runtime?.toDuration(DurationUnit.SECONDS)?.sensibleFormat(withSeconds = false)?.also {
            MovieDetailsInfoRow(stringResource(R.string.length), it)
        }
        details.year?.also {
            MovieDetailsInfoRow(stringResource(R.string.year)) {
                Text(it.toString(), modifier = Modifier.clickable { onYearClick(it) })
            }
        }
        details.director?.takeIfNotEmpty()?.also { directors ->
            MovieDetailsInfoRow(stringResource(R.string.directors)) {
                Column(horizontalAlignment = Alignment.End) {
                    for (director in directors) {
                        Text(director, modifier = Modifier.clickable { onPersonClick(director) })
                    }
                }
            }
        }
        details.country?.takeIfNotEmpty()?.also { countries ->
            MovieDetailsInfoRow(stringResource(R.string.countries)) {
                Column(horizontalAlignment = Alignment.End) {
                    for (country in countries) {
                        Text(country, modifier = Modifier.clickable { onCountryClick(country) })
                    }
                }
            }
        }
        details.genre?.takeIfNotEmpty()?.also { genres ->
            MovieDetailsInfoRow(stringResource(R.string.genres)) {
                Column(horizontalAlignment = Alignment.End) {
                    for (genre in genres) {
                        Text(genre, modifier = Modifier.clickable { onGenreClick(genre) })
                    }
                }
            }
        }
        details.originaltitle?.takeIfNotBlank()?.takeIf { it != details.displayTitle }?.also {
            MovieDetailsInfoRow(stringResource(R.string.original_title), it)
        }
        details.tag?.takeIfNotEmpty()?.also { tags ->
            MovieDetailsInfoRow(stringResource(R.string.tags)) {
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    for (tag in tags) {
                        Badge(
                            modifier = Modifier.Companion.padding(start = 5.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            content = { Text(tag) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieDetailsInfoRow(label: String, text: String) {
    MovieDetailsInfoRow(label = label) { Text(text) }
}

@Composable
fun MovieDetailsInfoRow(label: String, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            softWrap = false,
            maxLines = 1,
            modifier = Modifier.padding(end = 20.dp)
        )
        content()
    }
}
