package us.huseli.kiddo.compose.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.material.icons.sharp.Tv
import androidx.compose.material.icons.sharp.Visibility
import androidx.compose.material.icons.sharp.VisibilityOff
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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.CollapsedText
import us.huseli.kiddo.compose.ItemInfoRow
import us.huseli.kiddo.compose.LoadingFlash
import us.huseli.kiddo.compose.MediaDetailsBanner
import us.huseli.kiddo.compose.MediaDetailsCast
import us.huseli.kiddo.compose.MediaDetailsInfo
import us.huseli.kiddo.compose.MediaListItem
import us.huseli.kiddo.compose.controls.NiceLinearProgressIndicator
import us.huseli.kiddo.compose.controls.NiceOutlinedButton
import us.huseli.kiddo.compose.screens.tvshowdetails.EpisodeList
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.data.types.VideoDetailsSeason
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.videoRuntime
import us.huseli.kiddo.viewmodels.ItemLoadState
import us.huseli.kiddo.viewmodels.TvShowDetailsViewModel
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.extensions.takeIfNotEmpty

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TvShowDetailsScreen(
    onNavigate: (Routes) -> Unit,
    viewModel: TvShowDetailsViewModel = hiltViewModel(),
) {
    val banner by viewModel.banner.collectAsStateWithLifecycle()
    val episodes: List<VideoDetailsEpisode>? by viewModel.episodes.collectAsStateWithLifecycle()
    val headlineStyle = MaterialTheme.typography.titleLarge
    val poster by viewModel.poster.collectAsStateWithLifecycle()
    val seasons: List<VideoDetailsSeason>? by viewModel.seasons.collectAsStateWithLifecycle()
    val state: ItemLoadState<VideoDetailsTvShow> by viewModel.tvShowDetailsState.collectAsStateWithLifecycle()

    var expandedSeason by rememberSaveable { mutableStateOf<Int?>(null) }
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {}
    val details = state.item

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(horizontal = 10.dp),
    ) {
        if (state is ItemLoadState.Loading) item(span = { GridItemSpan(maxLineSpan) }) { LoadingFlash() }
        if (state is ItemLoadState.Error) item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                stringResource(
                    R.string.failed_to_get_tv_show_x,
                    (state as ItemLoadState.Error<*>).error
                ),
                modifier = Modifier.padding(top = 10.dp),
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Column(
                modifier = Modifier
                    .requiredWidth(screenWidth)
                    .padding(bottom = 10.dp)
            ) {
                MediaDetailsBanner(
                    title = details?.displayTitle ?: "",
                    headlineStyle = headlineStyle,
                    rating = details?.rating,
                    banner = banner,
                    thumbnail = poster,
                    votes = details?.votes,
                    thumbnailSize = DpSize(width = 100.dp, height = 140.dp),
                )
                NiceLinearProgressIndicator(progress = { details?.progress ?: 0f })
            }
        }

        details?.also {
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
        }

        details?.plot?.also { plot ->
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
                duration = details?.runtime?.videoRuntime(),
                year = details?.year?.takeIf { it > 0 }?.toString(),
                genres = details?.genre,
                tags = details?.tag,
                onGenreClick = { onNavigate(Routes.TvShowList(genre = it)) },
                onYearClick = { onNavigate(Routes.TvShowList(year = it)) },
                modifier = Modifier.padding(bottom = 30.dp),
            ) {
                details?.originaltitle?.takeIfNotBlank()?.takeIf { it != details.displayTitle }?.also {
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
            for ((seasonIdx, season) in seasons.withIndex()) {
                val episodes = episodes?.filter { it.season == season.season }?.takeIfNotEmpty()
                val runtime = episodes?.sumOf { it.runtime ?: 0 }?.takeIf { it > 0 }

                item(span = { GridItemSpan(maxLineSpan) }, key = "S${season.season}") {
                    val thumbnail by viewModel.flowImageBitmap(
                        season.art?.poster?.takeIfNotBlank()
                            ?: season.art?.thumb?.takeIfNotBlank()
                            ?: season.thumbnail?.takeIfNotBlank()
                    ).collectAsStateWithLifecycle()

                    MediaListItem(
                        title = season.displayTitle,
                        placeholderIcon = Icons.Sharp.Tv,
                        index = seasonIdx,
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
                        thumbnailSize = DpSize(width = 45.dp, height = 67.dp),
                    )
                }

                if (expandedSeason == season.season) {
                    episodes?.also { episodes ->
                        EpisodeList(
                            episodes = episodes,
                            season = season,
                            seasonCount = details?.season,
                            viewModel = viewModel,
                            onStreamClick = { launcher.launch(it) },
                        )
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        details?.cast?.takeIfNotEmpty()?.also {
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
