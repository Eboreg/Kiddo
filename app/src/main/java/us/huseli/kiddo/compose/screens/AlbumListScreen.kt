package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Album
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
import us.huseli.kiddo.compose.screens.albumdetails.AlbumListFilterDialog
import us.huseli.kiddo.data.types.ListSort
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.viewmodels.AlbumListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumListScreen(
    onAlbumDetailsClick: (Int) -> Unit,
    onSortAndFilter: (Routes.AlbumList) -> Unit,
    viewModel: AlbumListViewModel = hiltViewModel(),
) {
    val albums by viewModel.albums.collectAsStateWithLifecycle()
    val route by viewModel.route.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val listSort by viewModel.listSort.collectAsStateWithLifecycle()

    var isFilterDialogOpen by rememberSaveable { mutableStateOf(false) }
    var isSortDialogOpen by rememberSaveable { mutableStateOf(false) }

    if (isFilterDialogOpen) {
        AlbumListFilterDialog(
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

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
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
                    Text(stringResource(R.string.failed_to_get_albums_x, it), modifier = Modifier.padding(10.dp))
                }
            }

            itemsIndexed(albums, key = { _, album -> album.albumid }) { index, album ->
                var cover by remember(album) { mutableStateOf<ImageBitmap?>(null) }

                LaunchedEffect(album) {
                    cover = viewModel.getCover(album)
                }

                MediaListItem(
                    title = album.displayTitle,
                    index = index,
                    subTitle = album.artistString,
                    genres = album.allGenres,
                    supportingContent = album.supportingContent,
                    rating = album.rating,
                    thumbnail = cover,
                    isCurrentItem = false,
                    onClick = { onAlbumDetailsClick(album.albumid) },
                    placeholderIcon = Icons.Sharp.Album,
                    thumbnailSize = DpSize(100.dp, 100.dp),
                )
            }
        }
    }
}
