package us.huseli.kiddo.data.types

import us.huseli.kiddo.stripTags
import us.huseli.retaintheme.extensions.takeIfNotBlank

data class PlayerAudioStream(
    val bitrate: Int,
    val channels: Int,
    val codec: String,
    val index: Int,
    val isdefault: Boolean,
    val isimpaired: Boolean,
    val isoriginal: Boolean,
    val language: String,
    val name: String,
    val samplerate: Int,
) {
    val displayName: String
        get() = name.stripTags() + (language.takeIfNotBlank()?.let { " ($it)" } ?: "")
}
