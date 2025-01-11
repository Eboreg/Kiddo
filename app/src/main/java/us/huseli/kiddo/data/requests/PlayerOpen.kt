package us.huseli.kiddo.data.requests

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.enums.PlayerRepeat
import us.huseli.kiddo.data.requests.interfaces.IRequestStringResult
import us.huseli.kiddo.data.types.PlayerPositionTime
import us.huseli.kiddo.data.types.PlaylistItem
import us.huseli.retaintheme.extensions.filterValuesNotNull

abstract class AbstractPlayerOpen(val options: Options? = null) : IRequestStringResult {
    override val method: String = "Player.Open"

    override fun getParams(): Map<String, Any?> {
        val optionsMap = mapOf(
            "playername" to options?.playerName,
            "repeat" to options?.repeat,
            "shuffled" to options?.shuffled,
            "resume" to options?.resume?.let { it.time ?: it.percentage ?: it.resume },
        ).filterValuesNotNull()

        return mapOf("options" to optionsMap, "item" to getItem())
    }

    abstract fun getItem(): Map<String, Any?>

    data class Options(
        val playerName: String? = null,
        val repeat: PlayerRepeat? = null,
        val resume: Resume? = null,
        val shuffled: Boolean? = null,
    )

    data class Resume(
        val resume: Boolean? = null,
        val percentage: Double? = null,
        val time: PlayerPositionTime? = null,
    )
}

@Suppress("unused")
object PlayerOpen {
    class Playlist(val playlistId: Int, val position: Int? = null, options: Options? = null) :
        AbstractPlayerOpen(options) {
        override fun getItem(): Map<String, Any?> = mapOf("playlistid" to playlistId, "position" to position)
    }

    class Path(val path: String, val recursive: Boolean? = null, options: Options? = null) :
        AbstractPlayerOpen(options) {
        override fun getItem(): Map<String, Any?> = mapOf("path" to path, "recursive" to recursive)
    }

    class PartyMode(val type: Type? = null, val playlistPath: String? = null, options: Options? = null) :
        AbstractPlayerOpen(options) {
        enum class Type {
            @SerializedName("music") Music,
            @SerializedName("video") Video,
        }

        override fun getItem(): Map<String, Any?> = mapOf("partymode" to (type ?: requireNotNull(playlistPath)))
    }

    class Broadcast(val broadcastId: Int, options: Options? = null) : AbstractPlayerOpen(options) {
        override fun getItem(): Map<String, Any?> = mapOf("broadcastid" to broadcastId)
    }

    class Channel(val channelId: Int, options: Options? = null) : AbstractPlayerOpen(options) {
        override fun getItem(): Map<String, Any?> = mapOf("channelid" to channelId)
    }

    class Recording(val recordingId: Int, options: Options? = null) : AbstractPlayerOpen(options) {
        override fun getItem(): Map<String, Any?> = mapOf("recordingid" to recordingId)
    }

    class Item(
        val item: PlaylistItem,
        options: Options? = null,
    ) : AbstractPlayerOpen(options) {
        constructor(songId: Int? = null, albumId: Int? = null, movieId: Int? = null, options: Options? = null) :
            this(item = PlaylistItem(movieId = movieId, albumId = albumId, songId = songId), options = options)

        override fun getItem(): Map<String, Any?> = item.getParams()
    }
}
