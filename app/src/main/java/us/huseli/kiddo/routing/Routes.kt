package us.huseli.kiddo.routing

import kotlinx.serialization.Serializable
import us.huseli.kiddo.compose.MediaListFilterParameter
import us.huseli.kiddo.compose.getValue
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.ListFilterFieldsTvShows
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListSort
import us.huseli.retaintheme.extensions.takeIfNotBlank

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
        override val freetextFiltersFields: List<ListFilterFieldsAlbums> = listOf(
            ListFilterFieldsAlbums.Album,
            ListFilterFieldsAlbums.AlbumArtist,
            ListFilterFieldsAlbums.Artist,
            ListFilterFieldsAlbums.Genre,
            ListFilterFieldsAlbums.Year,
        )
        override val hasFilters: Boolean
            get() = listOfNotNull(artist, genre, year, style, theme, album).isNotEmpty()

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

        override fun getFilters(): List<ListFilter<ListFilterFieldsAlbums>> {
            return listOfNotNull(
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
        }
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
        override val freetextFiltersFields: List<ListFilterFieldsTvShows> = listOf(
            ListFilterFieldsTvShows.Director,
            ListFilterFieldsTvShows.Title,
            ListFilterFieldsTvShows.Actor,
            ListFilterFieldsTvShows.Year,
        )

        override val hasFilters: Boolean
            get() = listOfNotNull(person, genre, year, title).isNotEmpty()

        fun applyFilterParams(params: List<MediaListFilterParameter>) = copy(
            person = params.getValue("person"),
            title = params.getValue("title"),
            genre = params.getValue("genre"),
            year = params.getValue("year"),
        )

        fun applyListSort(listSort: ListSort) =
            copy(sortMethod = listSort.method, descending = listSort.order == ListSort.Order.Descending)

        override fun getFilters(): List<ListFilter<ListFilterFieldsTvShows>> {
            return listOfNotNull(
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
        override val freetextFiltersFields = listOf(
            ListFilterFieldsMovies.Director,
            ListFilterFieldsMovies.Title,
            ListFilterFieldsMovies.Actor,
            ListFilterFieldsMovies.Writers,
            ListFilterFieldsMovies.Year,
        )

        override val hasFilters: Boolean
            get() = listOfNotNull(person, genre, year, country, title).isNotEmpty()

        fun applyFilterParams(params: List<MediaListFilterParameter>) = copy(
            person = params.getValue("person"),
            title = params.getValue("title"),
            genre = params.getValue("genre"),
            year = params.getValue("year"),
            country = params.getValue("country"),
        )

        fun applyListSort(listSort: ListSort) =
            copy(sortMethod = listSort.method, descending = listSort.order == ListSort.Order.Descending)

        override fun getFilters(): List<ListFilter<ListFilterFieldsMovies>> {
            return listOfNotNull(
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
        }
    }
}
