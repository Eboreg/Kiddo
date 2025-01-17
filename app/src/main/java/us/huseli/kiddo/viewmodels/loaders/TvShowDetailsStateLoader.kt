package us.huseli.kiddo.viewmodels.loaders

import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.VideoDetailsTvShow

class TvShowDetailsStateLoader(val repository: Repository, val tvShowId: Int) :
    AbstractItemStateLoader<VideoDetailsTvShow>() {
    init {
        refresh()
    }

    override suspend fun getter(): VideoDetailsTvShow? = repository.getTvShowDetails(tvShowId)
}
