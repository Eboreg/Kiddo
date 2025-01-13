package us.huseli.kiddo

import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import us.huseli.kiddo.compose.App
import us.huseli.kiddo.viewmodels.AppViewModel
import us.huseli.retaintheme.ui.theme.RetainTheme
import us.huseli.retaintheme.utils.ILogger

@AndroidEntryPoint
class MainActivity : ComponentActivity(), ILogger {
    val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            RetainTheme {
                App()
            }
        }
    }

    override fun onRestart() {
        viewModel.setForeground(true)
        super.onRestart()
    }

    override fun onStop() {
        viewModel.setForeground(false)
        super.onStop()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                viewModel.decreaseVolume()
                true
            }

            KeyEvent.KEYCODE_VOLUME_UP -> {
                viewModel.increaseVolume()
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }
}
