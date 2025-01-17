package us.huseli.kiddo.compose.screens

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddToQueue
import androidx.compose.material.icons.sharp.Cast
import androidx.compose.material.icons.sharp.FileDownload
import androidx.compose.material.icons.sharp.FileDownloadOff
import androidx.compose.material.icons.sharp.Movie
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material.icons.sharp.Visibility
import androidx.compose.material.icons.sharp.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.Logger
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.CollapsedText
import us.huseli.kiddo.compose.DownloadProgress
import us.huseli.kiddo.compose.ItemInfoRow
import us.huseli.kiddo.compose.LoadingFlash
import us.huseli.kiddo.compose.MediaDetailsBanner
import us.huseli.kiddo.compose.MediaDetailsCast
import us.huseli.kiddo.compose.MediaDetailsInfo
import us.huseli.kiddo.compose.controls.NiceLinearProgressIndicator
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.kiddo.data.types.VideoDetailsMovie
import us.huseli.kiddo.formatFileSize
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.videoRuntime
import us.huseli.kiddo.viewmodels.ItemLoadState
import us.huseli.kiddo.viewmodels.MovieDetailsViewModel
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import us.huseli.retaintheme.snackbar.SnackbarEngine
import us.huseli.retaintheme.ui.theme.LocalBasicColors

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailsScreen(
    onNavigate: (Routes) -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val headlineStyle = MaterialTheme.typography.titleLarge
    val poster by viewModel.poster.collectAsStateWithLifecycle()
    val state by viewModel.movieDetailsState.collectAsStateWithLifecycle()
    val downloadProgress by viewModel.downloadProgress.collectAsStateWithLifecycle()
    val streamIntent by viewModel.streamIntent.collectAsStateWithLifecycle()
    val mimeType by viewModel.mimeType.collectAsStateWithLifecycle()
    val listItemFile by viewModel.listItemFile.collectAsStateWithLifecycle()
    val isDownloading by remember { derivedStateOf { downloadProgress != null } }
    val similarMovies by viewModel.similar.collectAsStateWithLifecycle()

    val streamLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val downloadLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(mimeType)) { uri ->
        uri?.also { viewModel.download(uri) }
    }
    val streamErrorMessage = stringResource(R.string.stream_error_message)
    val details = state.item

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        if (state is ItemLoadState.Loading) item(span = { GridItemSpan(maxLineSpan) }) { LoadingFlash() }
        if (state is ItemLoadState.Error) item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                stringResource(
                    R.string.failed_to_get_movie_x,
                    (state as ItemLoadState.Error<VideoDetailsMovie>).error
                ),
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.requiredWidth(LocalConfiguration.current.screenWidthDp.dp)) {
                MediaDetailsBanner(
                    title = details?.displayTitle ?: "",
                    headlineStyle = headlineStyle,
                    subTitle = details?.tagline,
                    rating = details?.rating,
                    banner = banner,
                    thumbnail = poster,
                    votes = details?.votes,
                    thumbnailSize = DpSize(width = 100.dp, height = 140.dp),
                )
                NiceLinearProgressIndicator(progress = { details?.progress ?: 0f })
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
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
                NiceOutlinedButton(
                    onClick = { viewModel.setWatched(details?.isWatched == false) },
                    leadingIcon = if (details?.isWatched == true) Icons.Sharp.VisibilityOff else Icons.Sharp.Visibility,
                    text = stringResource(if (details?.isWatched == true) R.string.set_unwatched else R.string.set_watched),
                    modifier = Modifier.padding(horizontal = 5.dp),
                )
                streamIntent?.also {
                    NiceOutlinedButton(
                        onClick = {
                            try {
                                streamLauncher.launch(it)
                            } catch (e: ActivityNotFoundException) {
                                Logger.logError(exception = e)
                                SnackbarEngine.addError(streamErrorMessage)
                            }
                        },
                        leadingIcon = Icons.Sharp.Cast,
                        text = stringResource(R.string.stream),
                        modifier = Modifier.padding(horizontal = 5.dp),
                    )
                }
                if (isDownloading) {
                    NiceOutlinedButton(
                        onClick = { viewModel.cancelDownload() },
                        leadingIcon = Icons.Sharp.FileDownloadOff,
                        text = stringResource(R.string.cancel_download),
                        modifier = Modifier.padding(horizontal = 5.dp),
                    )
                } else details?.file?.also { file ->
                    NiceOutlinedButton(
                        onClick = { downloadLauncher.launch(file.substringAfterLast('/')) },
                        leadingIcon = Icons.Sharp.FileDownload,
                        text = stringResource(R.string.download),
                        modifier = Modifier.padding(horizontal = 5.dp),
                    )
                }
            }
        }

        downloadProgress?.also { progress ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DownloadProgress(
                        progress = progress,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .weight(1f),
                    )
                    TextButton(
                        onClick = { viewModel.cancelDownload() },
                        content = { Text(stringResource(R.string.cancel)) },
                    )
                }
            }
        }

        details?.plot?.also { plot ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = stringResource(R.string.plot),
                        style = headlineStyle,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    CollapsedText(plot, maxLines = 3)
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            MediaDetailsInfo(
                headlineStyle = headlineStyle,
                personsLabel = stringResource(R.string.directors),
                duration = details?.runtime?.videoRuntime(),
                year = details?.year?.takeIf { it > 0 }?.toString(),
                persons = details?.director,
                genres = details?.genre,
                countries = details?.country,
                tags = details?.tag,
                onPersonClick = { onNavigate(Routes.MovieList(person = it)) },
                onGenreClick = { onNavigate(Routes.MovieList(genre = it)) },
                onYearClick = { onNavigate(Routes.MovieList(year = it)) },
                onCountryClick = { onNavigate(Routes.MovieList(country = it)) },
                modifier = Modifier.padding(bottom = 10.dp),
            ) {
                details?.originaltitle?.takeIfNotBlank()?.takeIf { it != details.displayTitle }?.also {
                    ItemInfoRow(stringResource(R.string.original_title), it)
                }
                listItemFile?.size?.takeIf { it > 0 }?.also { size ->
                    ItemInfoRow(stringResource(R.string.size), size.formatFileSize())
                }
            }
        }

        if (similarMovies.isNotEmpty()) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text("Similar movies", style = headlineStyle)
            }

            items(similarMovies, key = { "similar${it.movie.movieid}" }) { similar ->
                val background = LocalBasicColors.current.random()
                val poster by viewModel.flowImageBitmap(similar.movie.art?.poster?.takeIfNotBlank())
                    .collectAsStateWithLifecycle()

                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .clickable { onNavigate(Routes.MovieDetails(movieId = similar.movie.movieid)) },
                    shape = MaterialTheme.shapes.extraSmall,
                    colors = CardDefaults.cardColors(containerColor = background),
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        poster?.also {
                            Image(
                                bitmap = it,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        } ?: run {
                            Icon(
                                imageVector = Icons.Sharp.Movie,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(top = 5.dp)
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .align(Alignment.TopCenter),
                            )
                        }

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.9f),
                            content = {
                                Text(
                                    text = similar.movie.displayTitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(5.dp)
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                        )
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) { Spacer(modifier = Modifier.height(10.dp)) }
        }

        details?.cast?.takeIfNotEmpty()?.also {
            MediaDetailsCast(
                cast = it,
                headlineStyle = headlineStyle,
                memberModifier = Modifier.height(200.dp),
                onMemberClick = { onNavigate(Routes.MovieList(person = it)) },
                flowPortrait = { viewModel.flowImageBitmap(it) },
            )
        }
    }
}
