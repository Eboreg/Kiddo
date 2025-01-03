package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.extensions.launchOnMainThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel() {
    val isMuted = repository.isMuted
    val playerElapsedTimeString = repository.playerElapsedTimeString
    val playerPercentage = repository.playerPercentage
    val playerTotalTimeString = repository.playerTotalTimeString
    val volume = repository.volume

    fun seekFinish(percentage: Float) = launchOnMainThread { repository.seekFinish(percentage) }

    fun seekInProgress(percentage: Float) = launchOnMainThread { repository.seekInProgress(percentage) }

    fun sendKeypress(key: String) = launchOnIOThread { repository.sendKeypress(key) }

    fun setFullScreen(value: Boolean) = launchOnIOThread { repository.setFullScreen(value) }

    fun setMute(value: Boolean) = launchOnIOThread { repository.setMute(value) }

    fun setVolume(value: Int) = launchOnIOThread { repository.setVolume(value) }
}
