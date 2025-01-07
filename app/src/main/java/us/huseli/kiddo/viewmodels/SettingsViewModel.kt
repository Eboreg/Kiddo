package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel() {
    val hostname = repository.hostname
    val jsonPort = repository.jsonPort
    val password = repository.password
    val username = repository.username
    val websocketPort = repository.websocketPort

    fun update(hostname: String, username: String, password: String, jsonPort: String, websocketPort: String) {
        repository.updateSettings(
            hostname = hostname,
            username = username,
            password = password,
            jsonPort = jsonPort,
            websocketPort = websocketPort,
        )
    }
}
