package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.CollapsedText
import us.huseli.kiddo.compose.ItemInfoRow
import us.huseli.kiddo.compose.MediaDetailsBanner
import us.huseli.kiddo.compose.MediaDetailsInfo
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsCast
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.seconds
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.viewmodels.MovieDetailsViewModel

@Composable
fun MovieDetailsScreen(
    // onMovieListClick: (MovieListDestination.Route) -> Unit,
    onMovieListClick: (Routes.MovieList) -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val poster by viewModel.poster.collectAsStateWithLifecycle()
    val details by viewModel.movieDetails.collectAsStateWithLifecycle()
    val headlineStyle = MaterialTheme.typography.titleLarge
    val error by viewModel.error.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        error?.also {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(it, modifier = Modifier.padding(top = 10.dp))
            }
        }

        if (loading) item(span = { GridItemSpan(maxLineSpan) }) {
            Text(stringResource(R.string.loading_ellipsis), modifier = Modifier.padding(top = 10.dp))
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            MediaDetailsBanner(
                title = details.displayTitle,
                headlineStyle = headlineStyle,
                thumbnailWidth = 100.dp,
                subTitle = details.tagline,
                rating = details.rating,
                banner = banner,
                thumbnail = poster,
                votes = details.votes,
                onPlayClick = { viewModel.play() },
                onEnqueueClick = { viewModel.enqueue() },
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .requiredWidth(LocalConfiguration.current.screenWidthDp.dp)
            )
        }

        details.plot?.also { plot ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = stringResource(R.string.plot),
                        style = headlineStyle,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    CollapsedText(plot, maxLines = 5)
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            MediaDetailsInfo(
                headlineStyle = headlineStyle,
                personsLabel = stringResource(R.string.directors),
                duration = details.runtime?.seconds,
                year = details.year?.toString(),
                persons = details.director,
                genres = details.genre,
                countries = details.country,
                tags = details.tag,
                onPersonClick = { onMovieListClick(Routes.MovieList(person = it)) },
                onGenreClick = { onMovieListClick(Routes.MovieList(genre = it)) },
                onYearClick = { onMovieListClick(Routes.MovieList(year = it)) },
                onCountryClick = { onMovieListClick(Routes.MovieList(country = it)) },
                modifier = Modifier.padding(bottom = 10.dp),
            ) {
                details.originaltitle?.takeIfNotBlank()?.takeIf { it != details.displayTitle }?.also {
                    ItemInfoRow(stringResource(R.string.original_title), it)
                }
            }
        }

        details.cast?.also {
            MovieDetailsCast(
                cast = it,
                headlineStyle = headlineStyle,
                viewModel = viewModel,
                memberModifier = Modifier.height(200.dp),
                onMovieListClick = onMovieListClick,
            )
        }
    }
}
