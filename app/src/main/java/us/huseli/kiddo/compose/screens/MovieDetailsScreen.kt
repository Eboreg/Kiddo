package us.huseli.kiddo.compose.screens

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddToQueue
import androidx.compose.material.icons.sharp.Cast
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material.icons.sharp.Visibility
import androidx.compose.material.icons.sharp.VisibilityOff
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.Logger
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.CollapsedText
import us.huseli.kiddo.compose.ItemInfoRow
import us.huseli.kiddo.compose.MediaDetailsBanner
import us.huseli.kiddo.compose.MediaDetailsCast
import us.huseli.kiddo.compose.MediaDetailsInfo
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.videoRuntime
import us.huseli.kiddo.viewmodels.MovieDetailsViewModel
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.snackbar.SnackbarEngine

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MovieDetailsScreen(
    onNavigate: (Routes) -> Unit,
    viewModel: MovieDetailsViewModel = hiltViewModel(),
) {
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val poster by viewModel.poster.collectAsStateWithLifecycle()
    val details by viewModel.movieDetails.collectAsStateWithLifecycle()
    val headlineStyle = MaterialTheme.typography.titleLarge
    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val streamIntent by viewModel.streamIntent.collectAsStateWithLifecycle()
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val streamErrorMessage = stringResource(R.string.could_not_find_a_suitable_app_try_installing_vlc)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 10.dp),
    ) {
        exception?.also {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(stringResource(R.string.failed_to_get_movie_x, it), modifier = Modifier.padding(top = 10.dp))
            }
        }

        if (loading) item(span = { GridItemSpan(maxLineSpan) }) {
            Text(stringResource(R.string.loading_ellipsis), modifier = Modifier.padding(top = 10.dp))
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(modifier = Modifier.requiredWidth(LocalConfiguration.current.screenWidthDp.dp)) {
                MediaDetailsBanner(
                    title = details.displayTitle,
                    headlineStyle = headlineStyle,
                    thumbnailWidth = 100.dp,
                    subTitle = details.tagline,
                    rating = details.rating,
                    banner = banner,
                    thumbnail = poster,
                    votes = details.votes,
                )
                LinearProgressIndicator(
                    progress = { details.progress },
                    drawStopIndicator = {},
                    trackColor = MaterialTheme.colorScheme.primaryContainer,
                    color = MaterialTheme.colorScheme.primary,
                    gapSize = 0.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
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
                NiceOutlinedButton(
                    onClick = { viewModel.setWatched(!details.isWatched) },
                    leadingIcon = if (details.isWatched) Icons.Sharp.VisibilityOff else Icons.Sharp.Visibility,
                    text = stringResource(if (details.isWatched) R.string.set_unwatched else R.string.set_watched),
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
                streamIntent?.also {
                    NiceOutlinedButton(
                        onClick = {
                            try {
                                launcher.launch(it)
                            } catch (e: ActivityNotFoundException) {
                                Logger.logError(exception = e)
                                SnackbarEngine.addError(streamErrorMessage)
                            }
                        },
                        leadingIcon = Icons.Sharp.Cast,
                        text = stringResource(R.string.stream),
                        modifier = Modifier.padding(horizontal = 2.dp),
                    )
                }
            }
        }

        details.plot?.also { plot ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = stringResource(R.string.plot),
                        style = headlineStyle,
                        modifier = Modifier.padding(bottom = 5.dp)
                    )
                    CollapsedText(plot, maxLines = 5)
                }
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            MediaDetailsInfo(
                headlineStyle = headlineStyle,
                personsLabel = stringResource(R.string.directors),
                duration = details.runtime?.videoRuntime(),
                year = details.year?.takeIf { it > 0 }?.toString(),
                persons = details.director,
                genres = details.genre,
                countries = details.country,
                tags = details.tag,
                onPersonClick = { onNavigate(Routes.MovieList(person = it)) },
                onGenreClick = { onNavigate(Routes.MovieList(genre = it)) },
                onYearClick = { onNavigate(Routes.MovieList(year = it)) },
                onCountryClick = { onNavigate(Routes.MovieList(country = it)) },
                modifier = Modifier.padding(bottom = 10.dp),
            ) {
                details.originaltitle?.takeIfNotBlank()?.takeIf { it != details.displayTitle }?.also {
                    ItemInfoRow(stringResource(R.string.original_title), it)
                }
            }
        }

        details.cast?.also {
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
