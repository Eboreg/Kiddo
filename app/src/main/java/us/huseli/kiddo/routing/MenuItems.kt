package us.huseli.kiddo.routing

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.PlaylistPlay
import androidx.compose.material.icons.sharp.Album
import androidx.compose.material.icons.sharp.BugReport
import androidx.compose.material.icons.sharp.Gamepad
import androidx.compose.material.icons.sharp.Movie
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import us.huseli.kiddo.BuildConfig
import us.huseli.kiddo.R

sealed class MenuItems(val route: Routes, val icon: ImageVector, val label: Int, val debugOnly: Boolean = false) {
    object RemoteControl : MenuItems(Routes.RemoteControl, Icons.Sharp.Gamepad, R.string.remote_control)
    object Queue : MenuItems(Routes.Queue, Icons.AutoMirrored.Sharp.PlaylistPlay, R.string.queue)
    object MovieList : MenuItems(Routes.MovieList(), Icons.Sharp.Movie, R.string.movies)
    object AlbumList : MenuItems(Routes.AlbumList(), Icons.Sharp.Album, R.string.albums)
    object Settings : MenuItems(Routes.Settings, Icons.Sharp.Settings, R.string.settings)
    object Debug : MenuItems(Routes.Debug, Icons.Sharp.BugReport, R.string.debug, true)

    companion object : Iterable<MenuItems> {
        override fun iterator(): Iterator<MenuItems> {
            val items = listOf<MenuItems>(RemoteControl, Queue, MovieList, AlbumList, Settings, Debug)

            if (BuildConfig.DEBUG) return items.iterator()
            return items.filter { !it.debugOnly }.iterator()
        }
    }
}
