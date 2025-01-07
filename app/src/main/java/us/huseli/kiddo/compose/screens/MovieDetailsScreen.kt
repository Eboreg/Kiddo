package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.RatingStars
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsCast
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsInfo
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsPlot
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.viewmodels.MovieDetailsViewModel

@Composable
fun MovieDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val poster by viewModel.poster.collectAsStateWithLifecycle()
    val details by viewModel.movieDetails.collectAsStateWithLifecycle()
    val headlineStyle = MaterialTheme.typography.titleLarge

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            MovieDetailsBanner(
                details = details,
                banner = banner,
                poster = poster,
                headlineStyle = headlineStyle,
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp)
            )
        }
        details.plot?.also { plot ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                MovieDetailsPlot(
                    plot = plot,
                    headlineStyle = headlineStyle,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            MovieDetailsInfo(
                details = details,
                headlineStyle = headlineStyle,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
        details.cast?.also {
            MovieDetailsCast(
                cast = it,
                headlineStyle = headlineStyle,
                viewModel = viewModel,
                memberModifier = Modifier.height(200.dp),
            )
        }
    }
}

@Composable
fun MovieDetailsBanner(
    details: VideoDetailsMovie,
    headlineStyle: TextStyle,
    banner: ImageBitmap?,
    poster: ImageBitmap?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.height(160.dp)) {
        banner?.also {
            Image(
                bitmap = it,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                alpha = 0.3f,
                modifier = Modifier.fillMaxSize(),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 5.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(modifier = Modifier.width(100.dp)) {
                poster?.also {
                    Image(
                        bitmap = it,
                        modifier = Modifier.shadow(5.dp),
                        contentScale = ContentScale.FillWidth,
                        contentDescription = null,
                    )
                }
            }
            Card(
                shape = MaterialTheme.shapes.extraSmall,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.75f),
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .shadow(5.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(details.displayTitle, style = headlineStyle)
                    details.tagline?.also {
                        Text(text = it, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    }

                    if (details.rating != null) {
                        Column {
                            RatingStars(details.rating)
                            details.votes?.also {
                                Text(
                                    text = stringResource(R.string.x_votes, details.votes),
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
