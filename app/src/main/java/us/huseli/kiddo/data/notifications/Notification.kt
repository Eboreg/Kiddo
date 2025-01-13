package us.huseli.kiddo.data.notifications

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import us.huseli.kiddo.data.notifications.data.ApplicationOnVolumeChanged
import us.huseli.kiddo.data.notifications.data.AudioLibraryOnUpdate
import us.huseli.kiddo.data.notifications.data.InputOnInputRequested
import us.huseli.kiddo.data.notifications.data.PlayerNotificationsData
import us.huseli.kiddo.data.notifications.data.PlayerOnPropertyChanged
import us.huseli.kiddo.data.notifications.data.PlayerOnSeek
import us.huseli.kiddo.data.notifications.data.PlayerOnStop
import us.huseli.kiddo.data.notifications.data.PlaylistOnAdd
import us.huseli.kiddo.data.notifications.data.PlaylistOnClear
import us.huseli.kiddo.data.notifications.data.PlaylistOnRemove
import us.huseli.kiddo.data.notifications.data.VideoLibraryOnUpdate
import us.huseli.kiddo.data.notifications.interfaces.INotificationData
import us.huseli.retaintheme.utils.ILogger
import java.lang.reflect.Type

data class Notification<Data>(
    val method: String,
    val sender: String,
    val created: Long,
    val data: Data?,
) {
    class TypeAdapter : JsonDeserializer<Notification<*>> {
        private fun JsonElement.asDataObject(method: String?): INotificationData? {
            return takeIf { it.isJsonObject }?.asJsonObject?.let { obj ->
                val dataClass: Class<out INotificationData>? = when (method) {
                    "Application.OnVolumeChanged" -> ApplicationOnVolumeChanged::class.java
                    "Input.OnInputRequested" -> InputOnInputRequested::class.java
                    "Player.OnAVChange" -> PlayerNotificationsData::class.java
                    "Player.OnAVStart" -> PlayerNotificationsData::class.java
                    "Player.OnPause" -> PlayerNotificationsData::class.java
                    "Player.OnPlay" -> PlayerNotificationsData::class.java
                    "Player.OnPropertyChanged" -> PlayerOnPropertyChanged::class.java
                    "Player.OnResume" -> PlayerNotificationsData::class.java
                    "Player.OnSeek" -> PlayerOnSeek::class.java
                    "Player.OnSpeedChanged" -> PlayerNotificationsData::class.java
                    "Player.OnStop" -> PlayerOnStop::class.java
                    "Playlist.OnAdd" -> PlaylistOnAdd::class.java
                    "Playlist.OnRemove" -> PlaylistOnRemove::class.java
                    "Playlist.OnClear" -> PlaylistOnClear::class.java
                    "VideoLibrary.OnUpdate" -> VideoLibraryOnUpdate::class.java
                    "AudioLibrary.OnUpdate" -> AudioLibraryOnUpdate::class.java
                    else -> null
                }

                dataClass?.let { gson.fromJson(obj, it) }
            }
        }

        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?,
        ): Notification<*>? {
            val obj = json?.takeIf { it.isJsonObject }?.asJsonObject ?: run { return null }
            val method = obj.get("method").asString
            val params = obj.get("params").asJsonObject
            val dataElement = params.get("data")

            return Notification(
                method = method,
                sender = params.get("sender").asString,
                created = System.currentTimeMillis(),
                data = dataElement?.asDataObject(method) ?: dataElement,
            )
        }
    }

    companion object : ILogger {
        val gson: Gson = GsonBuilder()
            .registerTypeAdapter(Notification::class.java, TypeAdapter())
            .create()

        fun fromJson(json: String): Notification<*>? {
            try {
                val notification = gson.fromJson(json, Notification::class.java)

                log("Notification", "fromJson: json=$json", priority = Log.DEBUG)
                log("Notification", "fromJson: notification=$notification")
                return notification
            } catch (e: Throwable) {
                logError("Notification", "fromJson: json=$json", e)
                return null
            }
        }
    }
}
