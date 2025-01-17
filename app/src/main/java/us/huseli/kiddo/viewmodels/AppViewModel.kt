package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.enums.InputAction
import us.huseli.kiddo.data.notifications.Notification
import us.huseli.kiddo.data.notifications.data.InputOnInputRequested
import us.huseli.kiddo.managers.websocket.KodiNotificationListener
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel(),
    KodiNotificationListener {
    private val _inputRequest = MutableStateFlow<InputOnInputRequested?>(null)

    val askedNotificationPermission = repository.askedNotificationPermission
    val inputRequest = _inputRequest.asStateFlow()
    val currentPlayerItem = repository.playerItem
    val playerProperties = repository.playerProperties

    init {
        repository.registerNotificationListener(this)
    }

    fun cancelInputRequest() {
        launchOnIOThread { repository.executeInputAction(InputAction.Close) }
        _inputRequest.value = null
    }

    fun decreaseVolume() = launchOnIOThread { repository.decreaseVolume() }

    fun increaseVolume() = launchOnIOThread { repository.increaseVolume() }

    fun sendText(text: String) = launchOnIOThread {
        repository.sendText(text = text, done = true)
        _inputRequest.value = null
    }

    fun setAskedNotificationPermission() = repository.setAskedNotificationPermission()

    fun setForeground(value: Boolean) = repository.setForeground(value)

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }

    override fun onKodiNotification(notification: Notification<*>) {
        if (notification.data is InputOnInputRequested) {
            _inputRequest.value = notification.data
        } else if (notification.method == "Input.OnInputFinished") {
            _inputRequest.value = null
        }
    }
}
