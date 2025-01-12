package us.huseli.kiddo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import us.huseli.kiddo.data.AbstractRequest
import us.huseli.kiddo.data.enums.ApplicationPropertyName
import us.huseli.kiddo.data.enums.GuiPropertyName
import us.huseli.kiddo.data.enums.ListFieldsAll
import us.huseli.kiddo.data.enums.PlayerPropertyName
import us.huseli.kiddo.data.interfaces.IHasPlayerId
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.interfaces.IHasPlayerTime
import us.huseli.kiddo.data.interfaces.IHasPlayerTotalTime
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.ApplicationOnVolumeChanged
import us.huseli.kiddo.data.notifications.data.PlayerOnPropertyChanged
import us.huseli.kiddo.data.requests.ApplicationGetProperties
import us.huseli.kiddo.data.requests.ApplicationSetVolume
import us.huseli.kiddo.data.requests.GuiGetProperties
import us.huseli.kiddo.data.requests.JsonRpcPermission
import us.huseli.kiddo.data.requests.PlayerGetActivePlayers
import us.huseli.kiddo.data.requests.PlayerGetItem
import us.huseli.kiddo.data.requests.PlayerGetProperties
import us.huseli.kiddo.data.requests.PlayerSeek
import us.huseli.kiddo.data.requests.PlayerSetSubtitle
import us.huseli.kiddo.data.types.PlayerPropertyValue
import us.huseli.retaintheme.snackbar.SnackbarEngine
import us.huseli.retaintheme.utils.AbstractScopeHolder
import us.huseli.retaintheme.utils.ILogger
import kotlin.math.max

class Worker(
    private val jsonEngine: KodiJsonRpcEngine,
    private val websocketEngine: KodiWebsocketEngine,
    private val repository: Repository,
) : KodiNotificationListener, KodiResponseListener, AbstractScopeHolder(), ILogger {
    private val _latestRequestIds = mutableMapOf<String, Int>()
    private val _timestamp = MutableStateFlow<Long>(System.currentTimeMillis())

    fun initialize() {
        jsonEngine.registerListener(this)
        websocketEngine.registerListener(this)
        reset()

        // If hostname changes, do some reloading.
        launchOnIOThread {
            jsonEngine.hostname.filterNotNull().distinctUntilChanged().collect {
                reset()
            }
        }

        // Listen for error messages on JSON and websockets engines separately.
        launchOnMainThread {
            jsonEngine.connectErrorString.filterNotNull().collect { error ->
                SnackbarEngine.addError(error)
            }
        }
        launchOnMainThread {
            websocketEngine.connectErrorString.filterNotNull().collect { error ->
                SnackbarEngine.addError(error)
            }
        }

        // When playerId is changed for whatever reason, refetch player properties & current item.
        launchOnIOThread {
            repository.playerId.filterNotNull().distinctUntilChanged().collect { playerId ->
                fetchPlayerProperties(playerId)
                fetchPlayerItem(playerId)
            }
        }

        // Update playerElapsedTime in ~250ms intervals while playerSpeed != 0. Repository will ignore the request
        // if the user is currently seeking.
        launchOnMainThread {
            repository.playerSpeed.filterNotNull().collectLatest { speed ->
                while (true) {
                    val timestamp = System.currentTimeMillis()

                    if (speed != 0) {
                        repository.incrementPlayerElapsedTime((timestamp - _timestamp.value).times(speed).toLong())
                    }
                    _timestamp.value = timestamp
                    delay(250)
                }
            }
        }

        // Update playerProgress from playerElapsedTime and playerTotalTime if the latter is known. Repository will
        // ignore the request if user is currently seekking.
        launchOnMainThread {
            combine(
                repository.playerElapsedTime,
                repository.playerTotalTime.filterNotNull().filter { it > 0 },
            ) { elapsed, time ->
                elapsed.div(time.toFloat()).takeIf { !it.isNaN() } ?: 0f
            }.filterNotNull().distinctUntilChanged().collect { progress ->
                repository.updatePlayerProgress(progress)
            }
        }

        launchOnIOThread {
            jsonEngine.connectError.collect { error ->
                if (error == null) reset()
            }
        }
    }

    private suspend fun fetchGuiProperties() =
        jsonEngine.post(GuiGetProperties(properties = listOf(GuiPropertyName.Fullscreen)))

    private suspend fun fetchPlayerItem(playerId: Int) {
        val request = PlayerGetItem(
            playerId = playerId,
            properties = listOf(
                ListFieldsAll.Album,
                ListFieldsAll.AlbumId,
                ListFieldsAll.Artist,
                ListFieldsAll.CustomProperties,
                ListFieldsAll.Director,
                ListFieldsAll.Duration,
                ListFieldsAll.Fanart,
                ListFieldsAll.File,
                ListFieldsAll.Runtime,
                ListFieldsAll.Thumbnail,
                ListFieldsAll.Title,
            ),
        )

        jsonEngine.post(request)
    }

    private suspend fun fetchPlayerProperties(playerId: Int) {
        jsonEngine.post(
            PlayerGetProperties(
                playerId = playerId,
                properties = listOf(
                    PlayerPropertyName.AudioStreams,
                    PlayerPropertyName.CanChangeSpeed,
                    PlayerPropertyName.CanMove,
                    PlayerPropertyName.CanRepeat,
                    PlayerPropertyName.CanSeek,
                    PlayerPropertyName.CanShuffle,
                    PlayerPropertyName.CurrentAudioStream,
                    PlayerPropertyName.CurrentSubtitle,
                    PlayerPropertyName.Repeat,
                    PlayerPropertyName.Shuffled,
                    PlayerPropertyName.Speed,
                    PlayerPropertyName.SubtitleEnabled,
                    PlayerPropertyName.Subtitles,
                    PlayerPropertyName.Time,
                    PlayerPropertyName.TotalTime,
                    PlayerPropertyName.Type,
                ),
            ),
        )
    }

    private fun reset() {
        launchOnIOThread {
            jsonEngine.post(
                ApplicationGetProperties(
                    properties = listOf(
                        ApplicationPropertyName.Name,
                        ApplicationPropertyName.Volume,
                        ApplicationPropertyName.Muted,
                        ApplicationPropertyName.Version,
                        ApplicationPropertyName.Language,
                    ),
                ),
            )
        }
        launchOnIOThread { jsonEngine.post(PlayerGetActivePlayers()) }
        launchOnIOThread { fetchGuiProperties() }
        launchOnIOThread { jsonEngine.post(JsonRpcPermission()) }
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (listOf("Player.OnAVStart", "Player.OnResume", "Player.OnStop").contains(notification.method)) {
            repository.playerId.value?.also {
                launchOnIOThread {
                    fetchPlayerProperties(it)
                    if (notification.method != "Player.OnStop") fetchPlayerItem(it)
                    else repository.updatePlayerItem(null)
                }
            }
        }
        if (notification.method == "Player.OnAVChange") {
            launchOnIOThread { fetchGuiProperties() }
        }
        if (notification.data is ApplicationOnVolumeChanged) {
            repository.updateVolume(notification.data.volume)
            repository.updateIsMuted(notification.data.muted)
        }
        if (notification.data is PlayerOnPropertyChanged) {
            repository.mergePlayerProperties(notification.data.property)
        }
        if (notification.data is IHasPlayerSpeed) {
            notification.data.speed?.also { repository.updatePlayerSpeed(it) }
        }
        if (notification.data is IHasPlayerId) {
            repository.updatePlayerId(notification.data.playerid)
        }
        if (notification.data is IHasPlayerTime) {
            notification.data.time?.totalMilliseconds?.also { repository.updatePlayerElapsedTime(it) }
        }
    }

    override fun <Result : Any> onKodiRequestSucceeded(
        request: AbstractRequest<Result>,
        result: Result,
    ) {
        // If a request with the same method and a higher requestId has already been handled, don't handle this one
        // (they have probably come here in the wrong order due to network lag).
        val latestRequestId = _latestRequestIds.setOrMerge(request.method, request.requestId) { v1, v2 -> max(v1, v2) }

        if (latestRequestId > request.requestId) return

        if (request is ApplicationSetVolume && result is Int) {
            repository.updateVolume(result)
        }
        if (result is ApplicationGetProperties.Result) {
            result.volume?.also { repository.updateVolume(it) }
            result.muted?.also { repository.updateIsMuted(it) }
        }
        if (result is IHasPlayerSpeed) {
            result.speed?.also { repository.updatePlayerSpeed(it) }
        }
        if (result is PlayerPropertyValue) {
            repository.updatePlayerProperties(result)
        }
        if (result is IHasPlayerTotalTime) {
            result.totaltime?.totalMilliseconds?.takeIf { it > 0 }?.also { repository.updatePlayerTotalTime(it) }
        }
        if (result is IHasPlayerId) {
            repository.updatePlayerId(result.playerid)
        }
        if (result is IHasPlayerTime) {
            result.time?.totalMilliseconds?.also { millis ->
                repository.updatePlayerElapsedTime(millis, resetIsSeeking = result is PlayerSeek.Result)
            }
        }
        if (request is PlayerGetActivePlayers) {
            repository.updatePlayerId(request.result.firstOrNull()?.playerid)
        }
        if (request is PlayerGetItem) {
            repository.updatePlayerItem(request.result.item.takeIf { it.stringId != null })
        }
        if (request is ApplicationSetVolume) {
            repository.updateVolume(request.result)
        }
        if (request is PlayerSetSubtitle) {
            launchOnIOThread {
                // Seems like there is some lag between setting subtitles and player properties being correctly
                // updated, hence the ugly little arbitrary delay:
                delay(500)
                fetchPlayerProperties(playerId = request.playerId)
            }
        }
        if (request is JsonRpcPermission) {
            repository.updatePermissions(request.result)
        }
    }
}
