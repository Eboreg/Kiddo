package us.huseli.kiddo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import us.huseli.kiddo.dataclasses.notifications.KodiNotification
import us.huseli.kiddo.dataclasses.notifications.data.PlayerOnSeek
import us.huseli.kiddo.dataclasses.notifications.data.PlayerOnStop
import us.huseli.retaintheme.extensions.sensibleFormat
import us.huseli.retaintheme.utils.AbstractScopeHolder
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Singleton
class Repository @Inject constructor(
    private val jsonEngine: KodiJsonRpcEngine,
    private val websocketEngine: KodiWebsocketEngine,
) : KodiNotificationListener, AbstractScopeHolder() {
    private val _isSeeking = MutableStateFlow(false)
    private val _playerElapsedTime = MutableStateFlow(Duration.ZERO)
    private val _playerPercentage = MutableStateFlow(0f)
    private val _playerSpeed = merge(websocketEngine.playerSpeed, jsonEngine.playerSpeed)
    private val _timestamp = MutableStateFlow<Long>(System.currentTimeMillis())

    val host = jsonEngine.host
    val isMuted = merge(jsonEngine.isMuted, websocketEngine.isMuted).map { it == true }.stateWhileSubscribed(false)
    val isPlaying = _playerSpeed.map { it != 0.0 }.stateWhileSubscribed(false)
    val jsonPort = jsonEngine.port
    val playerElapsedTimeString =
        _playerElapsedTime.map { it.sensibleFormat() }.distinctUntilChanged().stateWhileSubscribed()
    val playerId = websocketEngine.playerId
    val playerPercentage = _playerPercentage.asStateFlow()
    val playerThumbnailImage = jsonEngine.playerThumbnailImage.stateWhileSubscribed()
    val playerTitle = jsonEngine.playerTitle.stateWhileSubscribed()
    val playerTotalTimeString =
        jsonEngine.playerTotalTime.map { it?.sensibleFormat() }.distinctUntilChanged().stateWhileSubscribed()
    val volume = merge(jsonEngine.volume, websocketEngine.volume).stateWhileSubscribed()
    val websocketPort = websocketEngine.port

    init {
        websocketEngine.addListener(this)

        launchOnIOThread {
            playerId.filterNotNull().distinctUntilChanged().collect { playerId ->
                jsonEngine.fetchPlayerProperties(playerId)
                jsonEngine.fetchPlayerItem(playerId)
            }
        }

        launchOnMainThread {
            combine(jsonEngine.playerElapsedTime, _isSeeking) { elapsed, isDragging ->
                elapsed?.takeIf { !isDragging }
            }.filterNotNull().distinctUntilChanged().collect { elapsed ->
                _playerElapsedTime.value = elapsed
            }
        }

        launchOnMainThread {
            combine(
                _playerElapsedTime,
                jsonEngine.playerTotalTime,
                _isSeeking,
            ) { elapsed, time, isDragging ->
                if (isDragging) null
                else time
                    ?.takeIf { it > Duration.ZERO }
                    ?.let { elapsed.div(it).times(100).toFloat() }
                    ?.takeIf { !it.isNaN() } ?: 0f
            }.filterNotNull().distinctUntilChanged().collect { percentage ->
                _playerPercentage.value = percentage
            }
        }

        launchOnMainThread {
            combine(_playerSpeed, _isSeeking) { speed, isDragging ->
                speed.takeIf { !isDragging }
            }.collectLatest { speed ->
                if (speed != null) {
                    while (true) {
                        delay(500)

                        val timestamp = System.currentTimeMillis()

                        _playerElapsedTime.value += (timestamp - _timestamp.value)
                            .toDuration(DurationUnit.MILLISECONDS)
                            .times(speed)
                        _timestamp.value = timestamp
                    }
                }
            }
        }
    }

    suspend fun playOrPause() {
        playerId.first()?.also { jsonEngine.playOrPause(it) }
    }

    suspend fun seekFinish(percentage: Float) {
        setPlayerElapsedTimeFromPercentage(percentage.toDouble())
        playerId.first()?.also { jsonEngine.seekPercentage(it, percentage) }
        _isSeeking.value = false
    }

    suspend fun sendKeypress(key: String) = jsonEngine.sendKeypress(key)

    suspend fun setFullScreen(value: Boolean) = jsonEngine.setFullScreen(value)

    fun setHost(value: String) = jsonEngine.setHost(value)

    fun setJsonPort(value: Int) = jsonEngine.setPort(value)

    suspend fun setMute(value: Boolean) = jsonEngine.setMute(value)

    suspend fun seekInProgress(percentage: Float) {
        _isSeeking.value = true
        _playerPercentage.value = percentage
        setPlayerElapsedTimeFromPercentage(percentage.toDouble())
    }

    suspend fun setVolume(value: Int) = jsonEngine.setVolume(value)

    fun setWebsocketPort(value: Int) = websocketEngine.setPort(value)

    suspend fun stop() {
        playerId.first()?.also { jsonEngine.stop(it) }
    }

    private suspend fun setPlayerElapsedTimeFromPercentage(percentage: Double) {
        jsonEngine.playerTotalTime.first()?.times(percentage / 100)?.also {
            _playerElapsedTime.value = it
        }
    }

    override fun onNotification(notification: KodiNotification<*>) {
        if (listOf("Player.OnAVStart", "Player.OnResume", "Player.OnStop").contains(notification.method)) {
            launchOnIOThread {
                playerId.first()?.also {
                    jsonEngine.fetchPlayerProperties(it)
                    jsonEngine.fetchPlayerItem(it)
                }
            }
        }

        if (notification.params.data is PlayerOnSeek) {
            notification.params.data.player.time?.duration?.also { _playerElapsedTime.value = it }
            _isSeeking.value = false
        } else if (notification.params.data is PlayerOnStop) {
            _playerPercentage.value = 0f
            _playerElapsedTime.value = Duration.ZERO
        }
    }
}
