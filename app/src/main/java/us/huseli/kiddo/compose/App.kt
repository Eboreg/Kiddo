package us.huseli.kiddo.compose

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Gamepad
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import us.huseli.kiddo.RemoteControlDestination
import us.huseli.kiddo.SettingsDestination
import us.huseli.kiddo.viewmodels.AppViewModel

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun App(viewModel: AppViewModel = hiltViewModel()) {
    val navController: NavHostController = rememberNavController()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val playerThumbnailImage by viewModel.playerThumbnailImage.collectAsStateWithLifecycle()
    val playerTitle by viewModel.playerTitle.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {},
                        content = { Icon(Icons.Sharp.Menu, null) },
                    )
                },
                actions = {
                    IconButton(
                        onClick = { navController.navigate(RemoteControlDestination.route()) },
                        content = { Icon(Icons.Sharp.Gamepad, null) },
                    )
                    IconButton(
                        onClick = { navController.navigate(SettingsDestination.route()) },
                        content = { Icon(Icons.Sharp.Settings, null) },
                    )
                },
            )
        },
        bottomBar = {
            BottomBar(
                isPlaying = isPlaying,
                playerThumbnailImage = playerThumbnailImage,
                playerTitle = playerTitle ?: "",
                onPlayOrPauseClick = { viewModel.playOrPause() },
            )
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            startDestination = RemoteControlDestination.route(),
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(route = RemoteControlDestination.route()) {
                RemoteControlScreen()
            }

            composable(route = SettingsDestination.route()) {
                SettingsScreen()
            }
        }
    }
}
