package us.huseli.kiddo.compose.screens.albumdetails

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import us.huseli.kiddo.R
import us.huseli.kiddo.data.AlbumFilterState
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.takeIfNotBlank

@Composable
fun AlbumListFilterDialog(
    route: Routes.AlbumList,
    onSubmit: (AlbumFilterState) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var artist by remember(route.artist) { mutableStateOf(route.artist ?: "") }
    var genre by remember(route.genre) { mutableStateOf(route.genre ?: "") }
    var year by remember(route.year) { mutableStateOf(route.year ?: "") }
    var style by remember(route.style) { mutableStateOf(route.style ?: "") }
    var album by remember(route.album) { mutableStateOf(route.album ?: "") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onSubmit(
                    AlbumFilterState(
                        artist = artist.takeIfNotBlank(),
                        genre = genre.takeIfNotBlank(),
                        year = year.takeIfNotBlank(),
                        style = style.takeIfNotBlank(),
                        album = album.takeIfNotBlank(),
                    )
                )
            }) { Text(stringResource(R.string.apply)) }
        },
        dismissButton = {
            TextButton(onClick = { onSubmit(AlbumFilterState()) }) { Text(stringResource(R.string.clear_all)) }
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) }
        },
        title = { Text(stringResource(R.string.filters)) },
        shape = MaterialTheme.shapes.small,
        text = {
            Column {
                OutlinedTextField(
                    value = album,
                    onValueChange = { album = it },
                    label = { Text(stringResource(R.string.title)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text(stringResource(R.string.artist)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = genre,
                    onValueChange = { genre = it },
                    label = { Text(stringResource(R.string.genre)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text(stringResource(R.string.year)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = style,
                    onValueChange = { style = it },
                    label = { Text(stringResource(R.string.style)) },
                    singleLine = true,
                )
            }
        },
    )
}
