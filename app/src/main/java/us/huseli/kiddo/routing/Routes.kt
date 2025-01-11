package us.huseli.kiddo.routing

import kotlinx.serialization.Serializable
import us.huseli.kiddo.data.AlbumFilterState
import us.huseli.kiddo.data.MovieFilterState
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.types.ListFilterAlbums
import us.huseli.kiddo.data.types.ListFilterMovies
import us.huseli.kiddo.data.types.ListSort

interface IMediaListRoute {
    val sortMethod: ListSort.Method?
    val descending: Boolean?

    fun getListSort(): ListSort? = sortMethod?.let {
        ListSort(
            method = it,
            order = if (descending == true) ListSort.Order.Descending else ListSort.Order.Ascending,
        )
    }

    fun hasFilters(): Boolean
}

@Serializable
sealed class Routes {
    @Serializable
    data class MovieDetails(val movieId: Int) : Routes()

    @Serializable
    data class AlbumDetails(val albumId: Int) : Routes()

    @Serializable
    data object RemoteControl : Routes()

    @Serializable
    data object Settings : Routes()

    @Serializable
    data object Queue : Routes()

    @Serializable
    data class AlbumList(
        val artist: String? = null,
        val genre: String? = null,
        val year: String? = null,
        val style: String? = null,
        val theme: String? = null,
        val album: String? = null,
        override val sortMethod: ListSort.Method? = ListSort.Method.Title,
        override val descending: Boolean? = null,
    ) : Routes(), IMediaListRoute {
        fun applyListSort(listSort: ListSort) =
            copy(sortMethod = listSort.method, descending = listSort.order == ListSort.Order.Descending)

        fun applyState(state: AlbumFilterState) = copy(
            artist = state.artist,
            genre = state.genre,
            year = state.year,
            album = state.album,
            style = state.style,
        )

        fun getFilter(): ListFilterAlbums? = ListFilterAlbums.and(
            genre?.let { ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.Genre) },
            artist?.let {
                ListFilterAlbums(
                    or = listOf(
                        ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.Artist),
                        ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.AlbumArtist),
                    )
                )
            },
            year?.let { ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.Year) },
            style?.let { ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.Styles) },
            theme?.let { ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.Themes) },
            album?.let { ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.Album) },
        )

        override fun hasFilters() = listOfNotNull(artist, genre, year, style, theme, album).isNotEmpty()
    }

    @Serializable
    data class MovieList(
        val person: String? = null,
        val genre: String? = null,
        val year: String? = null,
        val country: String? = null,
        val title: String? = null,
        override val sortMethod: ListSort.Method? = ListSort.Method.Title,
        override val descending: Boolean? = null,
    ) : Routes(), IMediaListRoute {
        fun applyListSort(listSort: ListSort) =
            copy(sortMethod = listSort.method, descending = listSort.order == ListSort.Order.Descending)

        fun applyState(state: MovieFilterState) = copy(
            person = state.person,
            genre = state.genre,
            year = state.year,
            country = state.country,
            title = state.title,
        )

        fun getFilter(): ListFilterMovies? = ListFilterMovies.and(
            genre?.let { ListFilterMovies(value = it, field = ListFilterFieldsMovies.Genre) },
            person?.let {
                ListFilterMovies(
                    or = listOf(
                        ListFilterMovies(value = it, field = ListFilterFieldsMovies.Director),
                        ListFilterMovies(value = it, field = ListFilterFieldsMovies.Actor),
                        ListFilterMovies(value = it, field = ListFilterFieldsMovies.Writers),
                    )
                )
            },
            year?.let { ListFilterMovies(value = it, field = ListFilterFieldsMovies.Year) },
            country?.let { ListFilterMovies(value = it, field = ListFilterFieldsMovies.Country) },
            title?.let { ListFilterMovies(value = it, field = ListFilterFieldsMovies.Title) },
        )

        override fun hasFilters() = listOfNotNull(person, genre, year, country, title).isNotEmpty()
    }
}
