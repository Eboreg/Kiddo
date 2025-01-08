package us.huseli.kiddo.routing

import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.serialization.Serializable

@Serializable
abstract class AbstractRoute(val name: String)

abstract class AbstractBaseMenuDestination<R : Any>(val icon: ImageVector, val label: Int) {
    abstract fun isOwnRoute(currentEntry: NavBackStackEntry?): Boolean
    abstract fun navigate(navController: NavHostController)

    @Composable
    fun MenuIconButton(navController: NavHostController) {
        val currentEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
        val colors =
            if (isOwnRoute(currentEntry)) IconButtonDefaults.filledIconButtonColors()
            else IconButtonDefaults.iconButtonColors()

        FilledIconButton(
            onClick = { navigate(navController) },
            colors = colors,
            content = { Icon(icon, stringResource(label)) },
        )
    }
}

abstract class AbstractMenuDestination<R : AbstractRoute>(icon: ImageVector, label: Int) :
    AbstractBaseMenuDestination<R>(icon, label) {
    abstract fun route(): R

    override fun isOwnRoute(currentEntry: NavBackStackEntry?): Boolean =
        currentEntry?.arguments?.getString("name") == route().name

    override fun navigate(navController: NavHostController) {
        navController.navigate(route())
    }
}

abstract class AbstractSimpleMenuDestination(val route: String, icon: ImageVector, label: Int) :
    AbstractBaseMenuDestination<String>(icon, label) {
    override fun isOwnRoute(currentEntry: NavBackStackEntry?): Boolean = currentEntry?.destination?.route == route

    override fun navigate(navController: NavHostController) {
        navController.navigate(route)
    }
}
