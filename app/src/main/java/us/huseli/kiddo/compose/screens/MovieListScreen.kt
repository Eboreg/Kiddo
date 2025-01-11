package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Videocam
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.MediaListItem
import us.huseli.kiddo.compose.controls.MediaListControls
import us.huseli.kiddo.compose.controls.MediaListSortDialog
import us.huseli.kiddo.compose.screens.movielist.MovieListFilterDialog
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.viewmodels.MovieListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieListScreen(
    onMovieDetailsClick: (Int) -> Unit,
    onSortAndFilter: (Routes.MovieList) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val currentItem by viewModel.currentItem.collectAsStateWithLifecycle()
    val route by viewModel.route.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val listSort by viewModel.listSort.collectAsStateWithLifecycle()

    var isFilterDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isSortDialogOpen by rememberSaveable { mutableStateOf(false) }

    if (isFilterDialogOpen) {
        MovieListFilterDialog(
            route = route,
            onSubmit = { onSortAndFilter(route.applyState(it)) },
            onDismissRequest = { isFilterDialogOpen = false },
        )
    }

    if (isSortDialogOpen) {
        MediaListSortDialog(
            sort = listSort,
            onSubmit = { onSortAndFilter(route.applyListSort(it)) },
            onDismissRequest = { isSortDialogOpen = false },
            methods = listOf(
                ListSort.Method.Title to stringResource(R.string.title),
                ListSort.Method.Year to stringResource(R.string.year),
                ListSort.Method.Rating to stringResource(R.string.rating),
                ListSort.Method.LastPlayed to stringResource(R.string.last_played),
                ListSort.Method.DateAdded to stringResource(R.string.date_added),
            ),
        )
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        stickyHeader {
            MediaListControls(
                onFilterClick = { isFilterDialogOpen = true },
                onSortClick = { isSortDialogOpen = true },
                hasFilters = route.hasFilters(),
            )
        }

        if (loading) item {
            Text(stringResource(R.string.loading_ellipsis), modifier = Modifier.padding(10.dp))
        }

        exception?.also {
            item {
                Text(stringResource(R.string.failed_to_get_movies_x, it), modifier = Modifier.padding(10.dp))
            }
        }

        itemsIndexed(movies, key = { _, movie -> movie.movieid }) { index, movie ->
            var poster by remember(movie) { mutableStateOf<ImageBitmap?>(null) }

            LaunchedEffect(movie) {
                poster = viewModel.getPoster(movie)
            }

            MediaListItem(
                title = movie.displayTitle,
                index = index,
                subTitle = movie.tagline,
                genres = movie.genre,
                supportingContent = movie.supportingContent,
                rating = movie.rating,
                thumbnail = poster,
                isCurrentItem = movie.movieid == currentItem?.id,
                onClick = { onMovieDetailsClick(movie.movieid) },
                placeholderIcon = Icons.Sharp.Videocam,
                thumbnailSize = DpSize(width = 60.dp, height = 100.dp),
            )
        }
    }
}
