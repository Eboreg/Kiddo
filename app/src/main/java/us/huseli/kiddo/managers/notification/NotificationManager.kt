package us.huseli.kiddo.managers.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import us.huseli.kiddo.R
import us.huseli.retaintheme.utils.AbstractScopeHolder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@RequiresApi(Build.VERSION_CODES.O)
class NotificationManager @Inject constructor(@ApplicationContext private val context: Context) :
    AbstractScopeHolder() {
    private val channel = NotificationChannel(
        CHANNEL_ID,
        context.getString(R.string.download_notifications),
        NotificationManager.IMPORTANCE_LOW,
    )
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val _notifications = mutableMapOf<Int, Notification>()

    init {
        notificationManager.createNotificationChannel(channel)
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
        _notifications.remove(id)
    }

    fun handle(data: NotificationData) {
        when (data) {
            is NotificationData.Cancel -> cancelNotification(data.id)

            is NotificationData.Update -> {
                _notifications[data.id]?.also { notification ->
                    if (
                        ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        val builder = Notification.Builder.recoverBuilder(context, notification).apply {
                            data.text?.also { setContentText(it) }
                            data.progress?.also { setProgress(100, it, false) }
                            data.title?.also { setContentTitle(it) }
                        }

                        notificationManager.notify(data.id, builder.build())
                    }
                }
            }

            is NotificationData.Add -> {
                if (
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val builder = Notification.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.media3_notification_small_icon)
                        .setContentTitle(data.title)
                        .setOngoing(data.ongoing)
                        .apply {
                            data.text?.also { setContentText(it) }
                            data.progress?.also { setProgress(100, it, false) }
                            data.action?.also { addAction(it) }
                        }
                    val notification = builder.build()

                    notificationManager.notify(data.id, notification)
                    _notifications[data.id] = notification
                }
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "kiddoNotifications"
    }
}
