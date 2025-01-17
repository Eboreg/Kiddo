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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.viewmodels.ItemLoadState
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.CollapsedText
import us.huseli.kiddo.compose.ItemInfoRow
import us.huseli.kiddo.compose.LoadingFlash
import us.huseli.kiddo.compose.MediaDetailsBanner
import us.huseli.kiddo.compose.MediaDetailsInfo
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.kiddo.compose.screens.albumdetails.AlbumDetailsSong
import us.huseli.kiddo.data.types.AudioDetailsAlbum
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
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val cover by viewModel.cover.collectAsStateWithLifecycle()
    val songs by viewModel.songs.collectAsStateWithLifecycle()
    val state by viewModel.albumDetailsState.collectAsStateWithLifecycle()

    val details = state.item
    val headlineStyle = MaterialTheme.typography.titleLarge

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MediaDetailsBanner(
            title = details?.displayTitle ?: "",
            headlineStyle = headlineStyle,
            subTitle = details?.artistString,
            rating = details?.rating,
            banner = banner,
            thumbnail = cover,
            votes = details?.votes,
            thumbnailSize = DpSize(140.dp, 140.dp),
            thumbnailContentScale = ContentScale.Crop,
        )

        if (state is ItemLoadState.Loading) {
            LoadingFlash(paddingTop = 20.dp)
        }
        if (state is ItemLoadState.Error) {
            Text(
                stringResource(
                    R.string.failed_to_get_album_x,
                    (state as ItemLoadState.Error<AudioDetailsAlbum>).error
                ),
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            NiceOutlinedButton(
                onClick = { viewModel.play() },
                leadingIcon = Icons.Sharp.PlayArrow,
                text = stringResource(R.string.play),
                modifier = Modifier.padding(horizontal = 5.dp),
            )
            NiceOutlinedButton(
                onClick = { viewModel.enqueue() },
                leadingIcon = Icons.Sharp.AddToQueue,
                text = stringResource(R.string.enqueue),
                modifier = Modifier.padding(horizontal = 5.dp),
            )
        }

        details?.description?.takeIfNotBlank()?.also { description ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
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
            duration = details?.albumduration?.seconds?.sensibleFormat(withSeconds = false),
            year = details?.year?.takeIf { it > 0 }?.toString(),
            persons = details?.artist,
            genres = details?.allGenres,
            onPersonClick = { onNavigate(Routes.AlbumList(artist = it)) },
            onGenreClick = { onNavigate(Routes.AlbumList(genre = it)) },
            onYearClick = { onNavigate(Routes.AlbumList(year = it)) },
            modifier = Modifier.padding(10.dp),
        ) {
            details?.mood?.takeIfNotEmpty()?.also { moods ->
                ItemInfoRow(stringResource(R.string.moods), moods)
            }
            details?.style?.takeIfNotEmpty()?.also { styles ->
                ItemInfoRow(
                    label = stringResource(R.string.styles),
                    texts = styles,
                    onClick = { onNavigate(Routes.AlbumList(style = it)) },
                )
            }
            details?.theme?.takeIfNotEmpty()?.also { themes ->
                ItemInfoRow(
                    label = stringResource(R.string.themes),
                    texts = themes,
                    onClick = { onNavigate(Routes.AlbumList(theme = it)) },
                )
            }
            details?.playcount?.also {
                ItemInfoRow(stringResource(R.string.play_count), it.toString())
            }
        }

        if (songs.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                Text(
                    text = stringResource(R.string.songs),
                    style = headlineStyle,
                    modifier = Modifier.padding(10.dp)
                )

                for ((index, song) in songs.withIndex()) {
                    AlbumDetailsSong(
                        index = index,
                        song = song,
                        albumArtists = details?.artist,
                        onPlayClick = { viewModel.playSong(song.songid) },
                        albumYear = details?.year,
                        totalDiscs = details?.totaldiscs,
                    )
                }
            }
        }
    }
}
