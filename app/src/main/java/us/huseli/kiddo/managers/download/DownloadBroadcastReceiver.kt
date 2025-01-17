package us.huseli.kiddo.managers.download

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import us.huseli.kiddo.Constants.CANCEL_DOWNLOAD_INTENT_ACTION
import javax.inject.Inject

@AndroidEntryPoint
class DownloadBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var downloadManager: DownloadManager

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == CANCEL_DOWNLOAD_INTENT_ACTION) {
            intent.getIntExtra("id", -1).takeIf { it > -1 }?.also { id ->
                downloadManager.cancel(id)
            }
        }
    }
}
