package us.huseli.kiddo.compose

import android.annotation.SuppressLint
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import us.huseli.kiddo.compose.controls.InputTextDialog
import us.huseli.kiddo.compose.screens.AlbumDetailsScreen
import us.huseli.kiddo.compose.screens.AlbumListScreen
import us.huseli.kiddo.compose.screens.DebugScreen
import us.huseli.kiddo.compose.screens.MovieDetailsScreen
import us.huseli.kiddo.compose.screens.MovieListScreen
import us.huseli.kiddo.compose.screens.QueueScreen
import us.huseli.kiddo.compose.screens.RemoteControlScreen
import us.huseli.kiddo.compose.screens.SettingsScreen
import us.huseli.kiddo.routing.MenuItems
import us.huseli.kiddo.routing.Routes
import us.huseli.kiddo.viewmodels.AppViewModel
import us.huseli.retaintheme.compose.SnackbarHosts

@SuppressLint("RestrictedApi")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: AppViewModel = hiltViewModel()) {
    val navController: NavHostController = rememberNavController()
    val inputRequest by viewModel.inputRequest.collectAsStateWithLifecycle()
    val onMovieDetailsClick = { movieId: Int ->
        navController.navigate(Routes.MovieDetails(movieId = movieId))
    }
    val onAlbumDetailsClick = { albumId: Int ->
        navController.navigate(Routes.AlbumDetails(albumId = albumId))
    }
    val currentEntry: NavBackStackEntry? by navController.currentBackStackEntryAsState()
    val currentPlayerItem by viewModel.currentPlayerItem.collectAsStateWithLifecycle()
    val playerProperties by viewModel.playerProperties.collectAsStateWithLifecycle()
    val remoteControlScreenActive = currentEntry?.destination?.hasRoute<Routes.RemoteControl>() == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    for (item in MenuItems) {
                        val isCurrentRoute = currentEntry?.destination?.hasRoute(item.route::class) == true
                        val colors =
                            if (isCurrentRoute) IconButtonDefaults.filledIconButtonColors()
                            else IconButtonDefaults.iconButtonColors()

                        FilledIconButton(
                            onClick = { navController.navigate(item.route) },
                            colors = colors,
                            content = { Icon(item.icon, stringResource(item.label)) },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHosts() },
        bottomBar = {
            if (!remoteControlScreenActive) {
                currentPlayerItem?.also {
                    PlayerPanel(
                        item = it,
                        properties = playerProperties,
                        onMovieDetailsClick = onMovieDetailsClick,
                        onAlbumDetailsClick = onAlbumDetailsClick,
                        collapsible = true,
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
                    )
                }
            }
        }
    ) { innerPadding ->
        inputRequest?.also {
            InputTextDialog(
                request = it,
                onDismissRequest = { viewModel.cancelInputRequest() },
                onSend = { viewModel.sendText(it) },
            )
        }

        NavHost(
            navController = navController,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            startDestination = Routes.RemoteControl,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<Routes.RemoteControl> {
                RemoteControlScreen(
                    onMovieDetailsClick = onMovieDetailsClick,
                    onAlbumDetailsClick = onAlbumDetailsClick,
                )
            }

            composable<Routes.Settings> {
                SettingsScreen()
            }

            composable<Routes.Queue> {
                QueueScreen()
            }

            composable<Routes.MovieList> {
                MovieListScreen(
                    onMovieDetailsClick = onMovieDetailsClick,
                    onSortAndFilter = { navController.navigate(it) },
                )
            }

            composable<Routes.MovieDetails> {
                MovieDetailsScreen(onMovieListClick = { navController.navigate(it) })
            }

            composable<Routes.AlbumList> {
                AlbumListScreen(
                    onAlbumDetailsClick = onAlbumDetailsClick,
                    onSortAndFilter = { navController.navigate(it) },
                )
            }

            composable<Routes.AlbumDetails> {
                AlbumDetailsScreen(onAlbumListClick = { navController.navigate(it) })
            }

            composable<Routes.Debug> {
                DebugScreen()
            }
        }
    }
}
