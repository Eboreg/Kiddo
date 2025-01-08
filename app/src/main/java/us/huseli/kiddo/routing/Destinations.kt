package us.huseli.kiddo.routing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.PlaylistPlay
import androidx.compose.material.icons.sharp.Album
import androidx.compose.material.icons.sharp.Gamepad
import androidx.compose.material.icons.sharp.Movie
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.serialization.Serializable
import us.huseli.kiddo.R
import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.types.ListFilterAlbums
import us.huseli.kiddo.data.types.ListFilterMovies
import us.huseli.kiddo.takeIfNotEmpty
import us.huseli.retaintheme.utils.ILogger

object RemoteControlDestination :
    AbstractSimpleMenuDestination("remote-control", Icons.Sharp.Gamepad, R.string.remote_control)

object SettingsDestination :
    AbstractSimpleMenuDestination("settings", Icons.Sharp.Settings, R.string.settings)

object QueueDestination : AbstractSimpleMenuDestination("queue", Icons.AutoMirrored.Sharp.PlaylistPlay, R.string.queue)

object MovieListDestination : AbstractMenuDestination<MovieListDestination.Route>(
    icon = Icons.Sharp.Movie,
    label = R.string.movies,
), ILogger {
    override fun route(): Route = Route()

    @Serializable
    data class Route(
        val person: String? = null,
        val genre: String? = null,
        val year: Int? = null,
        val country: String? = null,
    ) : AbstractRoute("movieList") {
        fun getFilter(): ListFilterMovies? {
            val filters = mutableListOf<ListFilterMovies>()

            if (genre != null) filters.add(ListFilterMovies(value = genre, field = ListFilterFieldsMovies.Genre))
            if (person != null) filters.add(
                ListFilterMovies(
                    or = listOf(
                        ListFilterMovies(value = person, field = ListFilterFieldsMovies.Director),
                        ListFilterMovies(value = person, field = ListFilterFieldsMovies.Actor),
                        ListFilterMovies(value = person, field = ListFilterFieldsMovies.Writers),
                    )
                )
            )
            if (year != null)
                filters.add(ListFilterMovies(value = year.toString(), field = ListFilterFieldsMovies.Year))
            if (country != null) filters.add(ListFilterMovies(value = country, field = ListFilterFieldsMovies.Country))

            return filters.takeIfNotEmpty()?.let { ListFilterMovies(and = it) }
        }

        @Composable
        fun getFilterStrings(): List<String> {
            return listOfNotNull(
                person?.let { stringResource(R.string.person_x, it) },
                genre?.let { stringResource(R.string.genre_x, it) },
                year?.let { stringResource(R.string.year_x, it) },
                country?.let { stringResource(R.string.country_x, it) },
            )
        }

        fun hasFilters(): Boolean = listOfNotNull(person, genre, year, country).isNotEmpty()
    }
}

object AlbumListDestination : AbstractMenuDestination<AlbumListDestination.Route>(
    icon = Icons.Sharp.Album,
    label = R.string.albums,
) {
    override fun route(): Route = Route()

    @Serializable
    data class Route(
        val artist: String? = null,
    ) : AbstractRoute("albumList") {
        fun getFilter(): ListFilterAlbums? {
            return artist?.let {
                ListFilterAlbums(
                    or = listOf(
                        ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.Artist),
                        ListFilterAlbums(value = it, field = ListFilterFieldsAlbums.AlbumArtist),
                    )
                )
            }
        }
    }
}
