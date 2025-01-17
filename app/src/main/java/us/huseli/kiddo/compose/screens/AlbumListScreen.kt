package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Album
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
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
import us.huseli.kiddo.viewmodels.AlbumListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumListScreen(
    onNavigate: (Routes) -> Unit,
    viewModel: AlbumListViewModel = hiltViewModel(),
) {
    val route = viewModel.route
    val albums = viewModel.pager.collectAsLazyPagingItems()

    val focusRequester = remember { FocusRequester() }
    var isFilterDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isSortDialogOpen by rememberSaveable { mutableStateOf(false) }

    if (isFilterDialogOpen) {
        MediaListFilterDialog(
            params = listOf(
                MediaListFilterParameter("album", route.album, stringResource(R.string.title)),
                MediaListFilterParameter("artist", route.artist, stringResource(R.string.artist)),
                MediaListFilterParameter("genre", route.genre, stringResource(R.string.genre)),
                MediaListFilterParameter("year", route.year, stringResource(R.string.year)),
                MediaListFilterParameter("style", route.style, stringResource(R.string.style)),
            ),
            onSubmit = { onNavigate(route.applyFilterParams(it)) },
            onDismissRequest = { isFilterDialogOpen = false },
        )
    }

    if (isSortDialogOpen) {
        MediaListSortDialog(
            sort = viewModel.route.listSort,
            onSubmit = { onNavigate(route.applyListSort(it)) },
            onDismissRequest = { isSortDialogOpen = false },
            methods = listOf(
                ListSort.Method.Artist to stringResource(R.string.artist),
                ListSort.Method.Title to stringResource(R.string.title),
                ListSort.Method.Year to stringResource(R.string.year),
                ListSort.Method.Rating to stringResource(R.string.rating),
                ListSort.Method.LastPlayed to stringResource(R.string.last_played),
                ListSort.Method.DateAdded to stringResource(R.string.date_added),
            ),
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (albums.loadState.refresh == LoadState.Loading) LoadingFlash()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
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

            (albums.loadState.refresh as? LoadState.Error)?.error?.also {
                item {
                    Text(stringResource(R.string.failed_to_get_albums_x, it), modifier = Modifier.padding(10.dp))
                }
            }

            items(count = albums.itemCount) { index ->
                albums[index]?.also { album ->
                    val cover by viewModel.flowCover(album).collectAsStateWithLifecycle()

                    MediaListItem(
                        title = album.displayTitle,
                        index = index,
                        subTitle = {
                            album.artistString?.also {
                                Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Italic)
                            }
                        },
                        genres = album.allGenres,
                        supportingContent = album.supportingContent,
                        rating = album.rating,
                        thumbnail = cover,
                        thumbnailSize = DpSize(width = 100.dp, height = 100.dp),
                        isCurrentItem = false,
                        onClick = { onNavigate(Routes.AlbumDetails(albumId = album.albumid)) },
                        placeholderIcon = Icons.Sharp.Album,
                    )
                }
            }
        }
    }
}
