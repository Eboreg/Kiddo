package us.huseli.kiddo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.sharp.PlaylistPlay
import androidx.compose.material.icons.sharp.Gamepad
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import us.huseli.kiddo.Constants.NAV_ARG_ITEM_ID
import us.huseli.retaintheme.utils.ILogger

abstract class AbstractMenuDestination(val route: String, val icon: ImageVector, val label: Int) : ILogger {
    @Composable
    fun MenuIconButton(navController: NavHostController) {
        val currentEntry by navController.currentBackStackEntryAsState()
        val colors =
            if (currentEntry?.destination?.route == route) IconButtonDefaults.filledIconButtonColors()
            else IconButtonDefaults.iconButtonColors()

        FilledIconButton(
            onClick = { navController.navigate(route) },
            colors = colors,
            content = { Icon(icon, stringResource(label)) },
        )
    }
}

abstract class AbstractDestination() {
    abstract val baseRoute: String
    abstract val arguments: List<NamedNavArgument>
    abstract val routeTemplate: String
}

object RemoteControlDestination :
    AbstractMenuDestination("remote-control", Icons.Sharp.Gamepad, R.string.remote_control)

object SettingsDestination :
    AbstractMenuDestination("settings", Icons.Sharp.Settings, R.string.settings)

object QueueDestination : AbstractMenuDestination("queue", Icons.AutoMirrored.Sharp.PlaylistPlay, R.string.queue)

object MovieDetailsDestination : AbstractDestination() {
    override val baseRoute = "item/movie"
    override val routeTemplate = "$baseRoute/{$NAV_ARG_ITEM_ID}"
    override val arguments = listOf(
        navArgument(NAV_ARG_ITEM_ID) { type = NavType.IntType },
    )

    fun route(movieId: Int) = "$baseRoute/$movieId"
}
