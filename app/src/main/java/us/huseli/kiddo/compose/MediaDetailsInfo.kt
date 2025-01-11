package us.huseli.kiddo.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R
import us.huseli.kiddo.sensibleFormat
import us.huseli.kiddo.takeIfNotEmpty
import kotlin.time.Duration

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaDetailsInfo(
    headlineStyle: TextStyle,
    personsLabel: String,
    duration: Duration? = null,
    year: String? = null,
    persons: Collection<String>? = null,
    genres: Collection<String>? = null,
    countries: Collection<String>? = null,
    tags: Collection<String>? = null,
    onPersonClick: ((String) -> Unit)? = null,
    onGenreClick: ((String) -> Unit)? = null,
    onYearClick: ((String) -> Unit)? = null,
    onCountryClick: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    extraContent: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(stringResource(R.string.information), style = headlineStyle)

        duration?.sensibleFormat(withSeconds = false)?.also {
            ItemInfoRow(stringResource(R.string.length), it)
        }
        year?.also {
            ItemInfoRow(stringResource(R.string.year), it.toString(), onYearClick)
        }
        persons?.takeIfNotEmpty()?.also { persons ->
            ItemInfoRow(personsLabel, persons, onPersonClick)
        }
        countries?.takeIfNotEmpty()?.also { countries ->
            ItemInfoRow(stringResource(R.string.countries), countries, onCountryClick)
        }
        genres?.takeIfNotEmpty()?.also { genres ->
            ItemInfoRow(stringResource(R.string.genres), genres, onGenreClick)
        }
        tags?.takeIfNotEmpty()?.also { tags ->
            ItemInfoRow(stringResource(R.string.tags)) {
                FlowRow(
                    horizontalArrangement = Arrangement.End,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    for (tag in tags) {
                        Badge(
                            modifier = Modifier.padding(start = 5.dp),
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            content = { Text(tag) },
                        )
                    }
                }
            }
        }
        extraContent()
    }
}
