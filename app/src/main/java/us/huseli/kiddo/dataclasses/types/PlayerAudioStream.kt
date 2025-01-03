package us.huseli.kiddo.dataclasses.types

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
)
