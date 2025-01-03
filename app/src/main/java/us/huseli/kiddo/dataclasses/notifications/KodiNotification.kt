package us.huseli.kiddo.dataclasses.notifications

import com.google.gson.Gson
import com.google.gson.JsonObject
import us.huseli.kiddo.dataclasses.notifications.data.ApplicationOnVolumeChanged
import us.huseli.kiddo.dataclasses.notifications.data.PlayerNotificationsData
import us.huseli.kiddo.dataclasses.notifications.data.PlayerOnSeek
import us.huseli.kiddo.dataclasses.notifications.data.PlayerOnStop
import us.huseli.kiddo.dataclasses.notifications.data.PlaylistOnAdd
import us.huseli.kiddo.dataclasses.notifications.data.PlaylistOnClear

data class KodiNotification<Data>(
    val jsonrpc: String,
    val method: String,
    val params: Params<Data>,
) {
    data class Params<Data>(
        val data: Data?,
        val sender: String,
    )

    companion object {
        fun fromJson(obj: JsonObject, gson: Gson): KodiNotification<*> {
            val method = obj.get("method").asString
            val params = obj.get("params").asJsonObject
            val rawData = params.get("data")?.takeIf { !it.isJsonNull }?.asJsonObject
            val dataClass = when (method) {
                "Application.OnVolumeChanged" -> ApplicationOnVolumeChanged::class.java
                "Player.OnAVChange" -> PlayerNotificationsData::class.java
                "Player.OnAVStart" -> PlayerNotificationsData::class.java
                "Player.OnPause" -> PlayerNotificationsData::class.java
                "Player.OnPlay" -> PlayerNotificationsData::class.java
                "Player.OnResume" -> PlayerNotificationsData::class.java
                "Player.OnSeek" -> PlayerOnSeek::class.java
                "Player.OnStop" -> PlayerOnStop::class.java
                "Playlist.OnAdd" -> PlaylistOnAdd::class.java
                "Playlist.OnClear" -> PlaylistOnClear::class.java
                else -> null
            }
            val data = if (dataClass != null && rawData != null) gson.fromJson(rawData, dataClass) else rawData

            return KodiNotification(
                jsonrpc = obj.get("jsonrpc").asString,
                method = method,
                params = Params(data = data, sender = params.get("sender").asString),
            )
        }
    }
}
