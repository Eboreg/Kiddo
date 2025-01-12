package us.huseli.kiddo.compose.screens.movielist

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
import us.huseli.kiddo.data.MovieFilterState
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.takeIfNotBlank

@Composable
fun MovieListFilterDialog(
    route: Routes.MovieList,
    onSubmit: (MovieFilterState) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var person by remember(route.person) { mutableStateOf(route.person ?: "") }
    var genre by remember(route.genre) { mutableStateOf(route.genre ?: "") }
    var year by remember(route.year) { mutableStateOf(route.year ?: "") }
    var country by remember(route.country) { mutableStateOf(route.country ?: "") }
    var title by remember(route.title) { mutableStateOf(route.title ?: "") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = {
                onSubmit(
                    MovieFilterState(
                        person = person.takeIfNotBlank(),
                        genre = genre.takeIfNotBlank(),
                        year = year.takeIfNotBlank(),
                        country = country.takeIfNotBlank(),
                        title = title.takeIfNotBlank(),
                    )
                )
            }) { Text(stringResource(R.string.apply)) }
        },
        dismissButton = {
            TextButton(onClick = { onSubmit(MovieFilterState()) }) { Text(stringResource(R.string.clear_all)) }
            TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.cancel)) }
        },
        title = { Text(stringResource(R.string.filters)) },
        shape = MaterialTheme.shapes.small,
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.title)) },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = person,
                    onValueChange = { person = it },
                    label = { Text(stringResource(R.string.person_director_actor_writer)) },
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
                    value = country,
                    onValueChange = { country = it },
                    label = { Text(stringResource(R.string.country)) },
                    singleLine = true,
                )
            }
        },
    )
}
