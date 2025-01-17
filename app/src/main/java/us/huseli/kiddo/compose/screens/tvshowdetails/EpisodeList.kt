package us.huseli.kiddo.compose.screens.tvshowdetails

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddToQueue
import androidx.compose.material.icons.sharp.Cast
import androidx.compose.material.icons.sharp.FileDownload
import androidx.compose.material.icons.sharp.FileDownloadOff
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import us.huseli.kiddo.Logger
import us.huseli.kiddo.R
import us.huseli.kiddo.compose.DownloadProgress
import us.huseli.kiddo.compose.MediaListItem
import us.huseli.kiddo.compose.controls.SmallFilledTonalIconButton
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.data.types.VideoDetailsSeason
import us.huseli.kiddo.videoRuntime
import us.huseli.kiddo.viewmodels.TvShowDetailsViewModel
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.snackbar.SnackbarEngine

@Suppress("FunctionName")
fun LazyGridScope.EpisodeList(
    episodes: List<VideoDetailsEpisode>,
    season: VideoDetailsSeason,
    seasonCount: Int?,
    viewModel: TvShowDetailsViewModel,
    onStreamClick: (Intent) -> Unit,
) {
    for ((episodeIdx, episode) in episodes.withIndex()) {
        item(span = { GridItemSpan(maxLineSpan) }, key = "S${season.season}E$episodeIdx") {
            val screenWidth = LocalConfiguration.current.screenWidthDp.dp
            val thumbnail by viewModel.flowImageBitmap(
                episode.art?.poster?.takeIfNotBlank()
                    ?: episode.art?.thumb?.takeIfNotBlank()
                    ?: episode.thumbnail?.takeIfNotBlank()
            ).collectAsStateWithLifecycle()
            val intent by viewModel.flowEpisodeStreamIntent(episode).collectAsStateWithLifecycle()
            val downloadProgress by viewModel.flowEpisodeDownloadProgress(episode.episodeid)
                .collectAsStateWithLifecycle()
            val downloadLauncher =
                rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("video/*")) { uri ->
                    uri?.also { viewModel.downloadEpisode(episode, uri) }
                }
            val isDownloading by remember { derivedStateOf { downloadProgress != null } }

            MediaListItem(
                title = episode.getDisplayTitle(seasonCount),
                titleMaxLines = 2,
                index = 1,
                thumbnail = thumbnail,
                thumbnailSize = DpSize(width = 75.dp, height = Dp.Unspecified),
                thumbnailPadding = PaddingValues(top = 10.dp),
                rating = episode.rating,
                progress = episode.progress,
                supportingContent = episode.runtime?.videoRuntime(),
                subTitle = {
                    Row(modifier = Modifier.padding(bottom = 10.dp)) {
                        SmallFilledTonalIconButton(
                            imageVector = Icons.Sharp.PlayArrow,
                            contentDescription = stringResource(R.string.play),
                            onClick = { viewModel.playEpisode(episode.episodeid) },
                        )
                        SmallFilledTonalIconButton(
                            imageVector = Icons.Sharp.AddToQueue,
                            contentDescription = stringResource(R.string.enqueue),
                            onClick = { viewModel.enqueueEpisode(episode) },
                        )
                        intent?.also {
                            val errorMessage = stringResource(R.string.stream_error_message)

                            SmallFilledTonalIconButton(
                                imageVector = Icons.Sharp.Cast,
                                contentDescription = stringResource(R.string.stream),
                                onClick = {
                                    try {
                                        onStreamClick(it)
                                    } catch (e: ActivityNotFoundException) {
                                        Logger.logError(exception = e)
                                        SnackbarEngine.addError(errorMessage)
                                    }
                                },
                            )
                        }
                        if (isDownloading) {
                            SmallFilledTonalIconButton(
                                imageVector = Icons.Sharp.FileDownloadOff,
                                contentDescription = stringResource(R.string.cancel_download),
                                onClick = { viewModel.cancelEpisodeDownload(episode.episodeid) },
                            )
                        } else episode.file?.also { file ->
                            SmallFilledTonalIconButton(
                                imageVector = Icons.Sharp.FileDownload,
                                contentDescription = stringResource(R.string.download),
                                onClick = { downloadLauncher.launch(file.substringAfterLast('/')) },
                            )
                        }
                    }

                    downloadProgress?.also { progress ->
                        DownloadProgress(
                            progress = progress,
                            modifier = Modifier.padding(bottom = 10.dp),
                        )
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
