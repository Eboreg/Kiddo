package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.launchOnMainThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerPanelViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel() {
    val isFullscreen = repository.isFullscreen
    val isMuted = repository.isMuted
    val isPlaying = repository.isPlaying
    val playerElapsedTime = repository.playerElapsedTimeSeconds
    val playerProgress = repository.playerProgress
    val playerThumbnail = repository.playerThumbnailImage
    val playerTotalTime = repository.playerTotalTimeSeconds
    val volume = repository.volume

    fun decreasePlayerSpeed() = launchOnIOThread { repository.decreasePlayerSpeed() }

    fun disableSubtitle() = launchOnIOThread { repository.disableSubtitle() }

    fun increasePlayerSpeed() = launchOnIOThread { repository.increasePlayerSpeed() }

    fun openSubtitleSearch() = launchOnIOThread { repository.openSubtitleSearch() }

    fun playOrPause() = launchOnIOThread { repository.playOrPause() }

    fun seekFinish(progress: Float) = launchOnMainThread { repository.seekFinish(progress) }

    fun seekInProgress(progress: Float) = launchOnMainThread { repository.seekInProgress(progress) }

    fun setAudioStream(index: Int) = launchOnIOThread { repository.setAudioStream(index) }

    fun setMute(value: Boolean) = launchOnIOThread { repository.setMute(value) }

    fun setSubtitle(index: Int) = launchOnIOThread { repository.setSubtitle(index) }

    fun setVolume(value: Int) = launchOnIOThread { repository.setVolume(value) }

    fun stop() = launchOnIOThread { repository.stop() }

    fun toggleFullscreen() = launchOnIOThread { repository.toggleFullscreen() }
}
