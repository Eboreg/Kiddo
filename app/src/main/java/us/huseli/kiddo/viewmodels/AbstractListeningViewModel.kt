package us.huseli.kiddo.viewmodels

import us.huseli.kiddo.KodiNotificationListener
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.utils.AbstractBaseViewModel

abstract class AbstractListeningViewModel(val repository: Repository) : AbstractBaseViewModel(),
    KodiNotificationListener {
    init {
        repository.registerNotificationListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        repository.unregisterNotificationListener(this)
    }
}
