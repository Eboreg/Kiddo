package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel() {
    val host = repository.host
    val jsonPort = repository.jsonPort
    val websocketPort = repository.websocketPort

    fun setHost(value: String) = repository.setHost(value)

    fun setJsonPort(value: Int) = repository.setJsonPort(value)

    fun setWebsocketPort(value: Int) = repository.setWebsocketPort(value)
}
