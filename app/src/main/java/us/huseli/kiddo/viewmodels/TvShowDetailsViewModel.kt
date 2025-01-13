package us.huseli.kiddo.viewmodels

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import us.huseli.kiddo.KodiNotificationListener
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.VideoLibraryOnUpdate
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.kiddo.data.types.VideoDetailsEpisode
import us.huseli.kiddo.data.types.VideoDetailsSeason
import us.huseli.kiddo.data.types.VideoDetailsTvShow
import us.huseli.kiddo.routing.Routes
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.takeIfNotBlank
import javax.inject.Inject

@HiltViewModel
class TvShowDetailsViewModel @Inject constructor(
    override val repository: Repository,
    savedStateHandle: SavedStateHandle,
) : AbstractSafeLoaderViewModel<VideoDetailsTvShow>(), KodiNotificationListener {
    private val _imageBitmaps = MutableStateFlow<Map<String, ImageBitmap?>>(emptyMap())
    private val _tvShowId = savedStateHandle.toRoute<Routes.TvShowDetails>().tvShowId
    private val _seasons = MutableStateFlow<List<VideoDetailsSeason>?>(null)
    private val _episodes = MutableStateFlow<List<VideoDetailsEpisode>?>(null)

    val tvShowDetails = data.filterNotNull().stateWhileSubscribed(VideoDetailsTvShow(tvshowid = _tvShowId, label = ""))

    val banner = tvShowDetails.map { details ->
        val path = details.art?.fanart?.takeIfNotBlank()
            ?: details.fanart?.takeIfNotBlank()
            ?: details.art?.banner?.takeIfNotBlank()

        path?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val poster = tvShowDetails.map { details ->
        details.art?.poster?.takeIfNotBlank()?.let { repository.getImageBitmap(it) }
    }.stateWhileSubscribed()
    val seasons = _seasons.asStateFlow()
    val episodes = _episodes.asStateFlow()

    init {
        repository.registerNotificationListener(this)
        launchOnIOThread { loadData() }
        launchOnIOThread { loadSeasons() }
        launchOnIOThread { loadEpisodes() }
    }

    fun enqueueEpisode(episode: VideoDetailsEpisode) = launchOnIOThread {
        repository.enqueueVideo(
            PlaylistItem(episodeId = episode.episodeid),
            episode.getDisplayTitle(_seasons.value?.size),
        )
    }

    fun flowEpisodeStreamIntent(episode: VideoDetailsEpisode) =
        (episode.file?.let { repository.flowStreamIntent(it, "video/*") } ?: emptyFlow()).stateWhileSubscribed()

    fun flowImageBitmap(path: String?) = _imageBitmaps.map { imageBitmaps ->
        if (path == null) null
        else if (imageBitmaps.containsKey(path)) imageBitmaps[path]
        else repository.getImageBitmap(path).also { _imageBitmaps.value += path to it }
    }.stateWhileSubscribed()

    fun playEpisode(episodeId: Int) = launchOnIOThread { repository.playEpisode(episodeId) }

    fun setWatched(value: Boolean) = launchOnIOThread { repository.setTvShowWatched(_tvShowId, value) }

    private suspend fun loadEpisodes() {
        _episodes.value = repository.listEpisodes(tvShowId = _tvShowId)
    }

    private suspend fun loadSeasons() {
        _seasons.value = repository.listSeasons(tvShowId = _tvShowId)
    }

    override suspend fun getData(): VideoDetailsTvShow? {
        return repository.getTvShowDetails(_tvShowId)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is VideoLibraryOnUpdate && notification.data.item.id == _tvShowId) {
            launchOnIOThread {
                loadData()
                loadSeasons()
                loadEpisodes()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }
}
