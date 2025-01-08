package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.compose.RatingStars
import us.huseli.kiddo.takeIfNotBlank
import us.huseli.kiddo.takeIfNotEmpty
import us.huseli.kiddo.viewmodels.MovieListViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieListScreen(
    onMovieDetailsClick: (Int) -> Unit,
    viewModel: MovieListViewModel = hiltViewModel(),
) {
    val movies by viewModel.movies.collectAsStateWithLifecycle()
    val currentItem by viewModel.currentItem.collectAsStateWithLifecycle()
    val route by viewModel.route.collectAsStateWithLifecycle()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (route.hasFilters() == true) {
            stickyHeader {
                Column(modifier = Modifier.padding(10.dp)) {
                    route.getFilterStrings().forEach {
                        Text(it, style = MaterialTheme.typography.titleLarge)
                    }
                }
            }
        }

        itemsIndexed(movies, key = { _, movie -> movie.movieid }) { index, movie ->
            var poster by remember(movie) { mutableStateOf<ImageBitmap?>(null) }

            LaunchedEffect(movie) {
                poster = viewModel.getPoster(movie)
            }

            ListItem(
                tonalElevation = 10.dp * (index % 2),
                headlineContent = {
                    Text(
                        text = movie.title?.takeIfNotBlank() ?: movie.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                },
                supportingContent = {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        movie.tagline?.let {
                            Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Italic)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            movie.genre?.takeIfNotEmpty()?.forEach { genre ->
                                Badge(containerColor = MaterialTheme.colorScheme.primaryContainer) {
                                    Text(genre)
                                }
                            }
                        }
                    }
                },
                overlineContent = {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            movie.supportingContent?.let {
                                Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        movie.rating?.takeIf { it > 0.0 }?.let { RatingStars(it, starSize = 12.dp) }
                    }
                },
                leadingContent = {
                    Box(modifier = Modifier.size(width = 60.dp, height = 100.dp)) {
                        poster?.also {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: run { Icon(Icons.Sharp.Videocam, null, modifier = Modifier.fillMaxSize()) }
                    }
                },
                modifier = Modifier
                    .clickable { onMovieDetailsClick(movie.movieid) }
                    .then(
                        if (movie.movieid == currentItem?.id) Modifier.border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                        ) else Modifier
                    ),
            )
        }
    }
}
