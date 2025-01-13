package us.huseli.kiddo.compose.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddToQueue
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.CollapsedText
import us.huseli.kiddo.compose.ItemInfoRow
import us.huseli.kiddo.compose.MediaDetailsBanner
import us.huseli.kiddo.compose.MediaDetailsInfo
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.kiddo.compose.screens.albumdetails.AlbumDetailsSong
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.sensibleFormat
import us.huseli.kiddo.viewmodels.AlbumDetailsViewModel
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlbumDetailsScreen(
    onNavigate: (Routes) -> Unit,
    viewModel: AlbumDetailsViewModel = hiltViewModel(),
) {
    val cover by viewModel.cover.collectAsStateWithLifecycle()
    val details by viewModel.albumDetails.collectAsStateWithLifecycle()
    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val headlineStyle = MaterialTheme.typography.titleLarge
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val songs by viewModel.songs.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        if (loading) Text(stringResource(R.string.loading_ellipsis), modifier = Modifier.padding(top = 10.dp))
        exception?.also {
            Text(stringResource(R.string.failed_to_get_album_x, it), modifier = Modifier.padding(top = 10.dp))
        }

        MediaDetailsBanner(
            title = details.displayTitle,
            headlineStyle = headlineStyle,
            thumbnailWidth = 140.dp,
            subTitle = details.artistString,
            rating = details.rating,
            banner = banner,
            thumbnail = cover,
            votes = details.votes,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            NiceOutlinedButton(
                onClick = { viewModel.play() },
                leadingIcon = Icons.Sharp.PlayArrow,
                text = stringResource(R.string.play),
                modifier = Modifier.padding(horizontal = 2.dp),
            )
            NiceOutlinedButton(
                onClick = { viewModel.enqueue() },
                leadingIcon = Icons.Sharp.AddToQueue,
                text = stringResource(R.string.enqueue),
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }

        details.description?.takeIfNotBlank()?.also { description ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = stringResource(R.string.description),
                    style = headlineStyle,
                    modifier = Modifier.padding(bottom = 5.dp),
                )
                CollapsedText(description, maxLines = 5)
            }
        }

        MediaDetailsInfo(
            headlineStyle = headlineStyle,
            personsLabel = stringResource(R.string.artists),
            duration = details.albumduration?.seconds?.sensibleFormat(withSeconds = false),
            year = details.year?.takeIf { it > 0 }?.toString(),
            persons = details.artist,
            genres = details.allGenres,
            onPersonClick = { onNavigate(Routes.AlbumList(artist = it)) },
            onGenreClick = { onNavigate(Routes.AlbumList(genre = it)) },
            onYearClick = { onNavigate(Routes.AlbumList(year = it)) },
            modifier = Modifier.padding(horizontal = 10.dp),
        ) {
            details.mood?.takeIfNotEmpty()?.also { moods ->
                ItemInfoRow(stringResource(R.string.moods), moods)
            }
            details.style?.takeIfNotEmpty()?.also { styles ->
                ItemInfoRow(
                    label = stringResource(R.string.styles),
                    texts = styles,
                    onClick = { onNavigate(Routes.AlbumList(style = it)) },
                )
            }
            details.theme?.takeIfNotEmpty()?.also { themes ->
                ItemInfoRow(
                    label = stringResource(R.string.themes),
                    texts = themes,
                    onClick = { onNavigate(Routes.AlbumList(theme = it)) },
                )
            }
            details.playcount?.also {
                ItemInfoRow(stringResource(R.string.play_count), it.toString())
            }
        }

        if (songs.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.songs),
                    style = headlineStyle,
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .padding(top = 10.dp, bottom = 5.dp),
                )

                for ((index, song) in songs.withIndex()) {
                    AlbumDetailsSong(
                        index = index,
                        song = song,
                        albumArtists = details.artist,
                        onPlayClick = { viewModel.playSong(song.songid) },
                        albumYear = details.year,
                    )
                }
            }
        }
    }
}
