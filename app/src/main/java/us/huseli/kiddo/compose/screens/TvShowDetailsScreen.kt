package us.huseli.kiddo.compose.screens

import android.content.ActivityNotFoundException
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.sharp.Tv
import androidx.compose.material.icons.sharp.Visibility
import androidx.compose.material.icons.sharp.VisibilityOff
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
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
import us.huseli.kiddo.compose.MediaListItem
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.videoRuntime
import us.huseli.kiddo.viewmodels.TvShowDetailsViewModel
import us.huseli.retaintheme.compose.SmallOutlinedButton
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import us.huseli.retaintheme.snackbar.SnackbarEngine

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TvShowDetailsScreen(
    onNavigate: (Routes) -> Unit,
    viewModel: TvShowDetailsViewModel = hiltViewModel(),
) {
    val details by viewModel.tvShowDetails.collectAsStateWithLifecycle()
    val exception by viewModel.exception.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val poster by viewModel.poster.collectAsStateWithLifecycle()
    val headlineStyle = MaterialTheme.typography.titleLarge
    val seasons by viewModel.seasons.collectAsStateWithLifecycle()
    val episodes by viewModel.episodes.collectAsStateWithLifecycle()
    var expandedSeason by rememberSaveable { mutableStateOf<Int?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val streamErrorMessage = stringResource(R.string.could_not_find_a_suitable_app_try_installing_vlc)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp),
    ) {
        exception?.also {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(stringResource(R.string.failed_to_get_tv_show_x, it), modifier = Modifier.padding(10.dp))
            }
        }

        if (loading) item(span = { GridItemSpan(maxLineSpan) }) {
            Text(stringResource(R.string.loading_ellipsis), modifier = Modifier.padding(10.dp))
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier
                    .requiredWidth(screenWidth)
                    .padding(bottom = 10.dp)
            ) {
                MediaDetailsBanner(
                    title = details.displayTitle,
                    headlineStyle = headlineStyle,
                    thumbnailWidth = 100.dp,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                NiceOutlinedButton(
                    onClick = { viewModel.setWatched(!details.isWatched) },
                    leadingIcon = if (details.isWatched) Icons.Sharp.VisibilityOff else Icons.Sharp.Visibility,
                    text = stringResource(if (details.isWatched) R.string.set_unwatched else R.string.set_watched),
                    modifier = Modifier.padding(horizontal = 2.dp),
                )
            }
        }

        details.plot?.also { plot ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(bottom = 15.dp)) {
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
                duration = details.runtime?.videoRuntime(),
                year = details.year?.takeIf { it > 0 }?.toString(),
                genres = details.genre,
                tags = details.tag,
                onGenreClick = { onNavigate(Routes.TvShowList(genre = it)) },
                onYearClick = { onNavigate(Routes.TvShowList(year = it)) },
                modifier = Modifier.padding(bottom = 30.dp),
            ) {
                details.originaltitle?.takeIfNotBlank()?.takeIf { it != details.displayTitle }?.also {
                    ItemInfoRow(stringResource(R.string.original_title), it)
                }
            }
        }

        seasons?.takeIfNotEmpty()?.also { seasons ->
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    stringResource(R.string.seasons),
                    style = headlineStyle,
                    modifier = Modifier.padding(bottom = 10.dp),
                )
            }
            for ((index, season) in seasons.withIndex()) {
                val episodes = episodes?.filter { it.season == season.season }?.takeIfNotEmpty()
                val runtime = episodes?.sumOf { it.runtime ?: 0 }?.takeIf { it > 0 }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    val thumbnail by viewModel.flowImageBitmap(
                        season.art?.poster?.takeIfNotBlank()
                            ?: season.art?.thumb?.takeIfNotBlank()
                            ?: season.thumbnail?.takeIfNotBlank()
                    ).collectAsStateWithLifecycle()

                    MediaListItem(
                        title = season.displayTitle,
                        placeholderIcon = Icons.Sharp.Tv,
                        index = index,
                        thumbnailWidth = 45.dp,
                        progress = season.progress,
                        supportingContent = listOfNotNull(
                            season.episode?.let { pluralStringResource(R.plurals.x_episodes, it, it) },
                            runtime?.videoRuntime(),
                        ).takeIfNotEmpty()?.joinToString(" · "),
                        thumbnail = thumbnail,
                        onClick = {
                            expandedSeason = if (expandedSeason == season.season) null else season.season
                        },
                        modifier = Modifier.requiredWidth(screenWidth),
                    )
                }
                if (expandedSeason == season.season) {
                    episodes?.also { episodes ->
                        for (episode in episodes) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                val thumbnail by viewModel.flowImageBitmap(
                                    episode.art?.poster?.takeIfNotBlank()
                                        ?: episode.art?.thumb?.takeIfNotBlank()
                                        ?: episode.thumbnail?.takeIfNotBlank()
                                ).collectAsStateWithLifecycle()
                                val intent by viewModel.flowEpisodeStreamIntent(episode).collectAsStateWithLifecycle()

                                MediaListItem(
                                    title = episode.getDisplayTitle(details.season),
                                    index = 1,
                                    thumbnail = thumbnail,
                                    thumbnailWidth = 75.dp,
                                    rating = episode.rating,
                                    progress = episode.progress,
                                    supportingContent = episode.runtime?.videoRuntime(),
                                    subTitle = {
                                        FlowRow(
                                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(10.dp),
                                        ) {
                                            SmallOutlinedButton(
                                                onClick = { viewModel.playEpisode(episode.episodeid) },
                                                leadingIcon = Icons.Sharp.PlayArrow,
                                                text = stringResource(R.string.play),
                                            )
                                            SmallOutlinedButton(
                                                onClick = { viewModel.enqueueEpisode(episode) },
                                                leadingIcon = Icons.Sharp.AddToQueue,
                                                text = stringResource(R.string.enqueue),
                                            )
                                            intent?.also {
                                                SmallOutlinedButton(
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
                                                )
                                            }
                                        }
                                        episode.plot?.also {
                                            Text(it, style = MaterialTheme.typography.bodySmall)
                                        }
                                    },
                                    modifier = Modifier
                                        .requiredWidth(screenWidth)
                                        .padding(start = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        details.cast?.takeIfNotEmpty()?.also {
            MediaDetailsCast(
                cast = it,
                headlineStyle = headlineStyle,
                memberModifier = Modifier
                    .padding(top = 10.dp)
                    .height(200.dp),
                onMemberClick = { onNavigate(Routes.TvShowList(person = it)) },
                flowPortrait = { viewModel.flowImageBitmap(it) },
            )
        }
    }
}
