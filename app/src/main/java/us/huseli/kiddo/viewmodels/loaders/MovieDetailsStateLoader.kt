package us.huseli.kiddo.viewmodels.loaders

import us.huseli.kiddo.Repository
import us.huseli.kiddo.data.types.VideoDetailsMovie

class MovieDetailsStateLoader(val repository: Repository, val movieId: Int) :
    AbstractItemStateLoader<VideoDetailsMovie>() {
    init {
        refresh()
    }

    override suspend fun getter(): VideoDetailsMovie? = repository.getMovieDetails(movieId)
}
