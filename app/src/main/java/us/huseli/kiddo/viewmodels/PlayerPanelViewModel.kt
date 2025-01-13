package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerPanelViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel() {
    val isMuted = repository.isMuted.stateWhileSubscribed(false)
    val isPlaying = repository.isPlaying.stateWhileSubscribed(false)
    val playerElapsedTime = repository.playerElapsedTimeSeconds.stateWhileSubscribed()
    val playerProgress = repository.playerProgress
    val playerThumbnail = repository.playerThumbnailImage.stateWhileSubscribed()
    val playerTotalTime = repository.playerTotalTimeSeconds
    val volume = repository.volume

    fun cycleRepeat() = launchOnIOThread { repository.cycleRepeat() }

    fun decreasePlayerSpeed() = launchOnIOThread { repository.decreasePlayerSpeed() }

    fun disableSubtitle() = launchOnIOThread { repository.disableSubtitle() }

    fun goToNextItem() = launchOnIOThread { repository.goToNextItem() }

    fun goToPreviousItem() = launchOnIOThread { repository.goToPreviousItem() }

    fun increasePlayerSpeed() = launchOnIOThread { repository.increasePlayerSpeed() }

    fun openSubtitleSearch() = launchOnIOThread { repository.openSubtitleSearch() }

    fun playOrPause() = launchOnIOThread { repository.playOrPause() }

    fun seekFinish(progress: Float) = repository.seekFinish(progress)

    fun seekInProgress(progress: Float) = repository.seekInProgress(progress)

    fun setAudioStream(index: Int) = launchOnIOThread { repository.setAudioStream(index) }

    fun setFullscreen() = launchOnIOThread { repository.setFullscreen() }

    fun setMute(value: Boolean) = launchOnIOThread { repository.setMute(value) }

    fun setSubtitle(index: Int) = launchOnIOThread { repository.setSubtitle(index) }

    fun setVolume(value: Int, rateLimited: Boolean = false) =
        launchOnIOThread { repository.setVolume(value, rateLimited) }

    fun stop() = launchOnIOThread { repository.stop() }

    fun toggleShuffle() = launchOnIOThread { repository.toggleShuffle() }
}
