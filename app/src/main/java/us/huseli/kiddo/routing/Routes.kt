package us.huseli.kiddo.routing

import kotlinx.serialization.Serializable
import us.huseli.kiddo.data.AlbumFilterState
import us.huseli.kiddo.data.MovieFilterState
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.ListFilterAlbums
import us.huseli.kiddo.data.types.ListFilterMovies
import us.huseli.kiddo.data.types.ListSort
import us.huseli.retaintheme.extensions.takeIfNotBlank

interface IMediaListRoute {
    val sortMethod: ListSort.Method?
    val descending: Boolean?
    val freetext: String?

    fun getListSort(): ListSort? = sortMethod?.let {
        ListSort(
            method = it,
            order = if (descending == true) ListSort.Order.Descending else ListSort.Order.Ascending,
        )
    }

    fun hasFilters(): Boolean
    fun hasSearch(): Boolean
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
    data object Debug : Routes()

    @Serializable
    data class AlbumList(
        val artist: String? = null,
        val genre: String? = null,
        val year: String? = null,
        val style: String? = null,
        val theme: String? = null,
        val album: String? = null,
        override val freetext: String? = null,
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

        fun getFilter(): ListFilterAlbums? {
            val filters = listOf(
                genre?.let { ListFilterAlbums(it, ListFilterFieldsAlbums.Genre) },
                artist?.let {
                    ListFilterAlbums(
                        or = listOf(
                            ListFilterAlbums(it, ListFilterFieldsAlbums.Artist),
                            ListFilterAlbums(it, ListFilterFieldsAlbums.AlbumArtist),
                        )
                    )
                },
                year?.let { ListFilterAlbums(it, ListFilterFieldsAlbums.Year) },
                style?.let { ListFilterAlbums(it, ListFilterFieldsAlbums.Styles) },
                theme?.let { ListFilterAlbums(it, ListFilterFieldsAlbums.Themes) },
                album?.let { ListFilterAlbums(it, ListFilterFieldsAlbums.Album) },
            )

            if (freetext?.takeIfNotBlank() != null) {
                return ListFilterAlbums.and(
                    *filters.toTypedArray(),
                    ListFilterAlbums.or(
                        ListFilterAlbums(freetext, ListFilterFieldsAlbums.Album, ListFilterOperators.Contains),
                        ListFilterAlbums(freetext, ListFilterFieldsAlbums.AlbumArtist, ListFilterOperators.Contains),
                        ListFilterAlbums(freetext, ListFilterFieldsAlbums.Artist, ListFilterOperators.Contains),
                        ListFilterAlbums(freetext, ListFilterFieldsAlbums.Genre, ListFilterOperators.Contains),
                        ListFilterAlbums(freetext, ListFilterFieldsAlbums.Year, ListFilterOperators.Contains),
                    ),
                )
            }

            return ListFilterAlbums.and(filters)
        }

        override fun hasFilters() = listOfNotNull(artist, genre, year, style, theme, album).isNotEmpty()
        override fun hasSearch() = freetext?.takeIfNotBlank() != null
    }

    @Serializable
    data class MovieList(
        val person: String? = null,
        val genre: String? = null,
        val year: String? = null,
        val country: String? = null,
        val title: String? = null,
        override val freetext: String? = null,
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

        fun getFilter(): ListFilterMovies? {
            val filters = listOf(
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

            if (freetext?.takeIfNotBlank() != null) {
                return ListFilterMovies.and(
                    *filters.toTypedArray(),
                    ListFilterMovies.or(
                        ListFilterMovies(freetext, ListFilterFieldsMovies.Director, ListFilterOperators.Contains),
                        ListFilterMovies(freetext, ListFilterFieldsMovies.Title, ListFilterOperators.Contains),
                        ListFilterMovies(freetext, ListFilterFieldsMovies.Actor, ListFilterOperators.Contains),
                        ListFilterMovies(freetext, ListFilterFieldsMovies.Writers, ListFilterOperators.Contains),
                        ListFilterMovies(freetext, ListFilterFieldsMovies.Year, ListFilterOperators.Contains),
                    )
                )
            }

            return ListFilterMovies.and(filters)
        }

        override fun hasFilters() = listOfNotNull(person, genre, year, country, title).isNotEmpty()
        override fun hasSearch() = freetext?.takeIfNotBlank() != null
    }
}
