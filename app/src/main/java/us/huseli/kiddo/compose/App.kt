package us.huseli.kiddo.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import us.huseli.kiddo.compose.screens.AlbumListScreen
import us.huseli.kiddo.routing.MovieDetailsRoute
import us.huseli.kiddo.routing.MovieListDestination
import us.huseli.kiddo.routing.QueueDestination
import us.huseli.kiddo.routing.RemoteControlDestination
import us.huseli.kiddo.routing.SettingsDestination
import us.huseli.kiddo.compose.screens.MovieDetailsScreen
import us.huseli.kiddo.compose.screens.MovieListScreen
import us.huseli.kiddo.compose.screens.QueueScreen
import us.huseli.kiddo.compose.screens.RemoteControlScreen
import us.huseli.kiddo.compose.screens.SettingsScreen
import us.huseli.kiddo.routing.AlbumListDestination
import us.huseli.kiddo.viewmodels.AppViewModel
import us.huseli.retaintheme.compose.SnackbarHosts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: AppViewModel = hiltViewModel()) {
    val navController: NavHostController = rememberNavController()
    val inputRequest by viewModel.inputRequest.collectAsStateWithLifecycle()
    val onMovieDetailsClick = { movieId: Int ->
        navController.navigate(MovieDetailsRoute(movieId = movieId))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    RemoteControlDestination.MenuIconButton(navController)
                    QueueDestination.MenuIconButton(navController)
                    SettingsDestination.MenuIconButton(navController)
                    MovieListDestination.MenuIconButton(navController)
                    AlbumListDestination.MenuIconButton(navController)
                },
            )
        },
        snackbarHost = { SnackbarHosts() },
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
            startDestination = RemoteControlDestination.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(route = RemoteControlDestination.route) {
                RemoteControlScreen(onMovieDetailsClick = onMovieDetailsClick)
            }

            composable(route = SettingsDestination.route) {
                SettingsScreen()
            }

            composable(route = QueueDestination.route) {
                QueueScreen()
            }

            composable<MovieListDestination.Route> {
                MovieListScreen(onMovieDetailsClick = onMovieDetailsClick)
            }

            composable<MovieDetailsRoute> {
                MovieDetailsScreen(
                    onPersonClick = { navController.navigate(MovieListDestination.Route(person = it)) },
                    onGenreClick = { navController.navigate(MovieListDestination.Route(genre = it)) },
                    onYearClick = { navController.navigate(MovieListDestination.Route(year = it)) },
                    onCountryClick = { navController.navigate(MovieListDestination.Route(country = it)) },
                )
            }

            composable<AlbumListDestination.Route> {
                AlbumListScreen()
            }
        }
    }
}
