package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class RemoteControlViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel() {
    val connectErrors = repository.connectErrorStrings.stateWhileSubscribed(emptyList())
    val connectStatus = repository.websocketStatus
    val hostname = repository.hostname
    val playerCoverImage = repository.playerCoverImage
    val playerItem = repository.playerItem
    val playerProperties = repository.playerProperties
    val volume = repository.volume

    fun sendKeypress(key: String) = launchOnIOThread { repository.sendKeypress(key) }
}
