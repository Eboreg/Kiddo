package us.huseli.kiddo.viewmodels

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.VideoLibraryOnUpdate
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.data.types.VideoDetailsSeason
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.managers.download.DownloadEvent
import us.huseli.kiddo.managers.download.DownloadListener
import us.huseli.kiddo.managers.download.DownloadProgress
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.viewmodels.loaders.TvShowDetailsStateLoader
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class TvShowDetailsViewModel @Inject constructor(
    val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractBaseViewModel(), KodiNotificationListener, DownloadListener {
    private val _downloadProgresses = MutableStateFlow<Map<Int, DownloadProgress>>(emptyMap())
    private val _downloadCancelIntents = mutableMapOf<Int, PendingIntent>()
    private val _imageBitmaps = MutableStateFlow<Map<String, ImageBitmap?>>(emptyMap())
    private val _tvShowId = savedStateHandle.toRoute<Routes.TvShowDetails>().tvShowId
    private val _seasons = MutableStateFlow<List<VideoDetailsSeason>?>(null)
    private val _episodes = MutableStateFlow<List<VideoDetailsEpisode>?>(null)
    private val _loader = TvShowDetailsStateLoader(repository, _tvShowId)
    private val _tvShowDetails =
        _loader.item.filterIsInstance<ItemLoadState.Loaded<VideoDetailsTvShow>>().map { it.item }
    private val _streamIntents = MutableStateFlow<Map<Int, Intent?>>(emptyMap())

    val tvShowDetailsState = _loader.item
    val banner = _tvShowDetails.map { details ->
        val path = details.art?.fanart?.takeIfNotBlank()
            ?: details.fanart?.takeIfNotBlank()
            ?: details.art?.banner?.takeIfNotBlank()

        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val poster = _tvShowDetails.map { details ->
        details.art?.poster?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val seasons = _seasons.asStateFlow()
    val episodes = _episodes.asStateFlow()

    init {
        repository.registerNotificationListener(this)
        repository.registerDownloadListener(this)
        launchOnIOThread { loadSeasons() }
        launchOnIOThread { loadEpisodes() }
    }

    fun cancelEpisodeDownload(episodeId: Int) {
        _downloadCancelIntents[episodeId]?.send()
    }

    fun downloadEpisode(episode: VideoDetailsEpisode, localUri: Uri) {
        launchOnIOThread {
            episode.file?.also { file ->
                repository.downloadFile(file, localUri, extra = episode)
            }
        }
    }

    fun enqueueEpisode(episode: VideoDetailsEpisode) = launchOnIOThread {
        repository.enqueueVideo(
            PlaylistItem(episodeId = episode.episodeid),
            episode.getDisplayTitle(_seasons.value?.size),
        )
    }

    fun flowEpisodeDownloadProgress(episodeId: Int) =
        _downloadProgresses.map { it[episodeId] }.stateWhileSubscribed()

    fun flowEpisodeStreamIntent(episode: VideoDetailsEpisode): StateFlow<Intent?> {
        return _streamIntents.map { intents ->
            if (intents.containsKey(episode.episodeid)) intents[episode.episodeid]
            else episode.file?.let { repository.getStreamIntent(it, "video/*", episode.title ?: episode.label) }
                .also { _streamIntents.value += episode.episodeid to it }
        }.stateWhileSubscribed()
    }

    fun flowImageBitmap(path: String?) = _imageBitmaps.map { imageBitmaps ->
        if (path == null) null
        else if (imageBitmaps.containsKey(path)) imageBitmaps[path]
        else repository.getImageBitmap(path).also { _imageBitmaps.value += path to it }
    }.stateWhileSubscribed()

    fun playEpisode(episodeId: Int) = launchOnIOThread { repository.playEpisode(episodeId) }

    fun setWatched(value: Boolean) = launchOnIOThread { repository.setTvShowWatched(_tvShowId, value) }

    private suspend fun loadEpisodes() {
        repository.listEpisodes(tvShowId = _tvShowId)?.items?.also { _episodes.value = it }
    }

    private suspend fun loadSeasons() {
        _seasons.value = repository.listSeasons(tvShowId = _tvShowId)?.items
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
        repository.unregisterDownloadListener(this)
    }

    override fun onDownloadEvent(event: DownloadEvent) {
        (event.data.extra as? VideoDetailsEpisode)?.also { episode ->
            if (episode.tvshowid == _tvShowId) {
                when (event) {
                    is DownloadEvent.Cancelled, is DownloadEvent.Finished -> {
                        _downloadProgresses.value -= episode.episodeid
                        _downloadCancelIntents -= episode.episodeid
                    }

                    is DownloadEvent.Progress -> _downloadProgresses.value += episode.episodeid to event.progress
                    is DownloadEvent.Started -> _downloadCancelIntents += episode.episodeid to event.cancelIntent
                }
            }
        }
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is VideoLibraryOnUpdate && notification.data.theId() == _tvShowId) {
            _loader.refresh()
            launchOnIOThread { loadSeasons() }
            launchOnIOThread { loadEpisodes() }
        }
    }
}
