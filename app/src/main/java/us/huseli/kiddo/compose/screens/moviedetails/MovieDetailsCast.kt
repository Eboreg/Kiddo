package us.huseli.kiddo.compose.screens.moviedetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import us.huseli.kiddo.R
import us.huseli.kiddo.data.types.VideoCast
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.viewmodels.MovieDetailsViewModel
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.ui.theme.LocalBasicColors

@Suppress("FunctionName")
fun LazyGridScope.MovieDetailsCast(
    cast: List<VideoCast>,
    headlineStyle: TextStyle,
    viewModel: MovieDetailsViewModel,
    modifier: Modifier = Modifier,
    memberModifier: Modifier = Modifier,
    onMovieListClick: (Routes.MovieList) -> Unit,
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(stringResource(R.string.cast), style = headlineStyle, modifier = modifier)
    }

    items(cast, key = { it.order }) { member ->
        var portrait by remember { mutableStateOf<ImageBitmap?>(null) }
        val background = LocalBasicColors.current.random()

        LaunchedEffect(member.thumbnail) {
            portrait = member.thumbnail?.takeIfNotBlank()?.let { viewModel.getActorPortrait(it) }
        }

        Card(
            modifier = memberModifier.clickable {
                onMovieListClick(Routes.MovieList(person = member.name))
            },
            shape = MaterialTheme.shapes.extraSmall,
            colors = CardDefaults.cardColors(containerColor = background),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                portrait?.also {
                    Image(
                        bitmap = it,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                } ?: run {
                    Icon(
                        imageVector = Icons.Sharp.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 5.dp)
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .align(Alignment.TopCenter),
                    )
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
                ) {
                    Column(
                        modifier = Modifier.padding(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            member.role,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(member.name, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
