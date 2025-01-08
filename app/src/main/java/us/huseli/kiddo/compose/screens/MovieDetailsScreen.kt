package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsBanner
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsCast
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsInfo
import us.huseli.kiddo.compose.screens.moviedetails.MovieDetailsPlot
import us.huseli.kiddo.viewmodels.MovieDetailsViewModel

@Composable
fun MovieDetailsScreen(
    onPersonClick: (String) -> Unit,
    onGenreClick: (String) -> Unit,
    onYearClick: (Int) -> Unit,
    onCountryClick: (String) -> Unit,
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
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            MovieDetailsBanner(
                details = details,
                banner = banner,
                poster = poster,
                headlineStyle = headlineStyle,
                onPlayClick = { viewModel.play() },
                onEnqueueClick = { viewModel.enqueue() },
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
                onPersonClick = onPersonClick,
                onGenreClick = onGenreClick,
                onYearClick = onYearClick,
                onCountryClick = onCountryClick,
            )
        }
        details.cast?.also {
            MovieDetailsCast(
                cast = it,
                headlineStyle = headlineStyle,
                viewModel = viewModel,
                memberModifier = Modifier.height(200.dp),
                onPersonClick = onPersonClick,
            )
        }
    }
}
