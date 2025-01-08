package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.sharp.Album
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
import us.huseli.kiddo.viewmodels.AlbumListViewModel

@Composable
fun AlbumListScreen(viewModel: AlbumListViewModel = hiltViewModel()) {
    val albums by viewModel.albums.collectAsStateWithLifecycle()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        itemsIndexed(albums, key = { _, album -> album.albumid }) { index, album ->
            var cover by remember(album) { mutableStateOf<ImageBitmap?>(null) }

            LaunchedEffect(album) {
                cover = viewModel.getCover(album)
            }

            ListItem(
                tonalElevation = 10.dp * (index % 2),
                headlineContent = {
                    Text(
                        text = album.title?.takeIfNotBlank() ?: album.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 5.dp),
                    )
                },
                supportingContent = {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        album.artistString?.let {
                            Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis, fontStyle = FontStyle.Italic)
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            album.genre?.takeIfNotEmpty()?.forEach { genre ->
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
                            album.supportingContent?.let {
                                Text(it, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                        album.rating?.takeIf { it > 0.0 }?.let { RatingStars(it, starSize = 12.dp) }
                    }
                },
                leadingContent = {
                    Box(modifier = Modifier.size(100.dp)) {
                        cover?.also {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } ?: run { Icon(Icons.Sharp.Album, null, modifier = Modifier.fillMaxSize()) }
                    }
                },
            )
        }
    }
}
