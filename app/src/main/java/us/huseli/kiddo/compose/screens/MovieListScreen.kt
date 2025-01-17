package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Videocam
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.LoadingFlash
import us.huseli.kiddo.compose.MediaListFilterDialog
import us.huseli.kiddo.compose.MediaListFilterParameter
import us.huseli.kiddo.compose.MediaListItem
import us.huseli.kiddo.compose.controls.MediaListControls
import us.huseli.kiddo.compose.controls.MediaListSortDialog
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.videoRuntime
import us.huseli.kiddo.viewmodels.MovieListViewModel
import us.huseli.retaintheme.extensions.takeIfNotEmpty

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieListScreen(
    onNavigate: (Routes) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    val movies = viewModel.pager.collectAsLazyPagingItems()
    val currentItem by viewModel.currentItem.collectAsStateWithLifecycle()
    val route = viewModel.route
    val focusRequester = remember { FocusRequester() }

    var isFilterDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isSortDialogOpen by rememberSaveable { mutableStateOf(false) }

    if (isFilterDialogOpen) {
        MediaListFilterDialog(
            params = listOf(
                MediaListFilterParameter("title", route.title, stringResource(R.string.title)),
                MediaListFilterParameter("person", route.person, stringResource(R.string.person_director_actor_writer)),
                MediaListFilterParameter("genre", route.genre, stringResource(R.string.genre)),
                MediaListFilterParameter("year", route.year, stringResource(R.string.year)),
                MediaListFilterParameter("country", route.country, stringResource(R.string.country)),
            ),
            onSubmit = { onNavigate(route.applyFilterParams(it)) },
            onDismissRequest = { isFilterDialogOpen = false },
        )
    }

    if (isSortDialogOpen) {
        MediaListSortDialog(
            sort = route.listSort,
            onSubmit = { onNavigate(route.applyListSort(it)) },
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

    Box(modifier = Modifier.fillMaxSize()) {
        if (movies.loadState.refresh == LoadState.Loading) LoadingFlash()

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            stickyHeader {
                MediaListControls(
                    onFilterClick = { isFilterDialogOpen = true },
                    onSortClick = { isSortDialogOpen = true },
                    hasFilters = route.hasFilters,
                    showSearch = route.hasSearch,
                    freetext = route.freetext ?: "",
                    onSearch = { onNavigate(route.copy(freetext = it)) },
                    focusRequester = focusRequester,
                )
            }

            (movies.loadState.refresh as? LoadState.Error)?.error?.also {
                item {
                    Text(stringResource(R.string.failed_to_get_movies_x, it), modifier = Modifier.padding(10.dp))
                }
            }

            items(count = movies.itemCount) { index ->
                movies[index]?.also { movie ->
                    val poster by viewModel.flowPoster(movie).collectAsStateWithLifecycle()
                    val minutes = movie.runtime?.videoRuntime()
                    val supportingContent = remember(movie, minutes) {
                        listOfNotNull(minutes, movie.year?.takeIf { it > 0 }?.toString())
                            .takeIfNotEmpty()
                            ?.joinToString(" · ")
                    }

                    MediaListItem(
                        title = movie.displayTitle,
                        index = index,
                        genres = movie.genre,
                        supportingContent = supportingContent,
                        rating = movie.rating,
                        thumbnail = poster,
                        isCurrentItem = movie.movieid == currentItem?.id,
                        onClick = { onNavigate(Routes.MovieDetails(movieId = movie.movieid)) },
                        placeholderIcon = Icons.Sharp.Videocam,
                        progress = movie.progress,
                        thumbnailSize = DpSize(width = 60.dp, height = 90.dp),
                    )
                }
            }
        }
    }
}