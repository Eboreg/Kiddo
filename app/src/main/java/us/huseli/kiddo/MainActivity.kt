package us.huseli.kiddo

import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dagger.hilt.android.AndroidEntryPoint
import us.huseli.kiddo.Constants.CANCEL_DOWNLOAD_INTENT_ACTION
import us.huseli.kiddo.compose.App
import us.huseli.kiddo.managers.download.DownloadBroadcastReceiver
import us.huseli.kiddo.viewmodels.AppViewModel
import us.huseli.retaintheme.ui.theme.RetainTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    val viewModel: AppViewModel by viewModels()
    val downloadBroadcastReceiver = DownloadBroadcastReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        ContextCompat.registerReceiver(
            this,
            downloadBroadcastReceiver,
            IntentFilter(CANCEL_DOWNLOAD_INTENT_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )

        setContent {
            RetainTheme {
                App()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(downloadBroadcastReceiver)
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

    override fun onRestart() {
        viewModel.setForeground(true)
        super.onRestart()
    }

    override fun onStop() {
        viewModel.setForeground(false)
        super.onStop()
    }
}
