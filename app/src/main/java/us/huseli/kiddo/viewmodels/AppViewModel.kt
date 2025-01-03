package us.huseli.kiddo.viewmodels

import dagger.hilt.android.lifecycle.HiltViewModel
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.extensions.launchOnIOThread
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(private val repository: Repository) : AbstractBaseViewModel() {
    val isPlaying = repository.isPlaying
    val playerPercentage = repository.playerPercentage
    val playerThumbnailImage = repository.playerThumbnailImage
    val playerTitle = repository.playerTitle

    fun playOrPause() = launchOnIOThread { repository.playOrPause() }
}
