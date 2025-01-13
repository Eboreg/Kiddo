package us.huseli.kiddo.routing

import kotlinx.serialization.Serializable
import us.huseli.kiddo.compose.MediaListFilterParameter
import us.huseli.kiddo.compose.getValue
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.ListFilterFieldsTvShows
import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListSort
import us.huseli.retaintheme.extensions.takeIfNotBlank

interface IMediaListRoute<Field : Any> {
    val sortMethod: ListSort.Method?
    val descending: Boolean?
    val freetext: String?

    fun getListSort(): ListSort? = sortMethod?.let {
        ListSort(
            method = it,
            order = if (descending == true) ListSort.Order.Descending else ListSort.Order.Ascending,
        )
    }

    fun hasSearch() = freetext?.takeIfNotBlank() != null

    fun getFilter(): ListFilter<Field>?
    fun hasFilters(): Boolean
}

@Serializable
sealed class Routes {
    @Serializable
    data class MovieDetails(val movieId: Int) : Routes()

    @Serializable
    data class AlbumDetails(val albumId: Int) : Routes()

    @Serializable
    data class TvShowDetails(val tvShowId: Int) : Routes()

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
        val album: String? = null,
        val artist: String? = null,
        val genre: String? = null,
        val style: String? = null,
        val theme: String? = null,
        val year: String? = null,
        override val freetext: String? = null,
        override val sortMethod: ListSort.Method? = ListSort.Method.Title,
        override val descending: Boolean? = null,
    ) : Routes(), IMediaListRoute<ListFilterFieldsAlbums> {
        fun applyFilterParams(params: List<MediaListFilterParameter>) = copy(
            album = params.getValue("album"),
            artist = params.getValue("artist"),
            genre = params.getValue("genre"),
            style = params.getValue("style"),
            theme = params.getValue("theme"),
            year = params.getValue("year"),
        )

        fun applyListSort(listSort: ListSort) =
            copy(sortMethod = listSort.method, descending = listSort.order == ListSort.Order.Descending)

        override fun getFilter(): ListFilter<ListFilterFieldsAlbums>? {
            val filters = listOf(
                genre?.let { ListFilter(it, ListFilterFieldsAlbums.Genre) },
                artist?.let {
                    ListFilter(
                        or = listOf(
                            ListFilter(it, ListFilterFieldsAlbums.Artist),
                            ListFilter(it, ListFilterFieldsAlbums.AlbumArtist),
                        )
                    )
                },
                year?.let { ListFilter(it, ListFilterFieldsAlbums.Year) },
                style?.let { ListFilter(it, ListFilterFieldsAlbums.Styles) },
                theme?.let { ListFilter(it, ListFilterFieldsAlbums.Themes) },
                album?.let { ListFilter(it, ListFilterFieldsAlbums.Album) },
            )

            if (freetext?.takeIfNotBlank() != null) {
                return ListFilter.and(
                    *filters.toTypedArray(),
                    ListFilter.or(
                        ListFilter(freetext, ListFilterFieldsAlbums.Album, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsAlbums.AlbumArtist, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsAlbums.Artist, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsAlbums.Genre, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsAlbums.Year, ListFilterOperators.Contains),
                    ),
                )
            }

            return ListFilter.and(filters)
        }

        override fun hasFilters() = listOfNotNull(artist, genre, year, style, theme, album).isNotEmpty()
    }

    @Serializable
    data class TvShowList(
        val person: String? = null,
        val genre: String? = null,
        val year: String? = null,
        val title: String? = null,
        override val freetext: String? = null,
        override val sortMethod: ListSort.Method? = ListSort.Method.Title,
        override val descending: Boolean? = null,
    ) : Routes(), IMediaListRoute<ListFilterFieldsTvShows> {
        override fun hasFilters(): Boolean = listOfNotNull(person, genre, year, title).isNotEmpty()

        fun applyFilterParams(params: List<MediaListFilterParameter>) = copy(
            person = params.getValue("person"),
            title = params.getValue("title"),
            genre = params.getValue("genre"),
            year = params.getValue("year"),
        )

        fun applyListSort(listSort: ListSort) =
            copy(sortMethod = listSort.method, descending = listSort.order == ListSort.Order.Descending)

        override fun getFilter(): ListFilter<ListFilterFieldsTvShows>? {
            val filters = listOf(
                title?.takeIfNotBlank()?.let { ListFilter(value = it, field = ListFilterFieldsTvShows.Title) },
                person?.takeIfNotBlank()?.let {
                    ListFilter(
                        or = listOf(
                            ListFilter(value = it, field = ListFilterFieldsTvShows.Director),
                            ListFilter(value = it, field = ListFilterFieldsTvShows.Actor),
                        ),
                    )
                },
                year?.takeIfNotBlank()?.let { ListFilter(value = it, field = ListFilterFieldsTvShows.Year) },
                genre?.takeIfNotBlank()?.let { ListFilter(value = it, field = ListFilterFieldsTvShows.Genre) },
            )

            if (freetext?.takeIfNotBlank() != null) {
                return ListFilter.and(
                    *filters.toTypedArray(),
                    ListFilter.or(
                        ListFilter(freetext, ListFilterFieldsTvShows.Director, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsTvShows.Title, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsTvShows.Actor, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsTvShows.Year, ListFilterOperators.Contains),
                    )
                )
            }

            return ListFilter.and(filters)
        }
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
    ) : Routes(), IMediaListRoute<ListFilterFieldsMovies> {
        fun applyFilterParams(params: List<MediaListFilterParameter>) = copy(
            person = params.getValue("person"),
            title = params.getValue("title"),
            genre = params.getValue("genre"),
            year = params.getValue("year"),
            country = params.getValue("country"),
        )

        fun applyListSort(listSort: ListSort) =
            copy(sortMethod = listSort.method, descending = listSort.order == ListSort.Order.Descending)

        override fun getFilter(): ListFilter<ListFilterFieldsMovies>? {
            val filters = listOf(
                genre?.let { ListFilter(value = it, field = ListFilterFieldsMovies.Genre) },
                person?.let {
                    ListFilter(
                        or = listOf(
                            ListFilter(value = it, field = ListFilterFieldsMovies.Director),
                            ListFilter(value = it, field = ListFilterFieldsMovies.Actor),
                            ListFilter(value = it, field = ListFilterFieldsMovies.Writers),
                        )
                    )
                },
                year?.let { ListFilter(value = it, field = ListFilterFieldsMovies.Year) },
                country?.let { ListFilter(value = it, field = ListFilterFieldsMovies.Country) },
                title?.let { ListFilter(value = it, field = ListFilterFieldsMovies.Title) },
            )

            if (freetext?.takeIfNotBlank() != null) {
                return ListFilter.and(
                    *filters.toTypedArray(),
                    ListFilter.or(
                        ListFilter(freetext, ListFilterFieldsMovies.Director, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsMovies.Title, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsMovies.Actor, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsMovies.Writers, ListFilterOperators.Contains),
                        ListFilter(freetext, ListFilterFieldsMovies.Year, ListFilterOperators.Contains),
                    )
                )
            }

            return ListFilter.and(filters)
        }

        override fun hasFilters() = listOfNotNull(person, genre, year, country, title).isNotEmpty()
    }
}
