package us.huseli.kiddo.dataclasses.types

data class PlayerVideoStream(
    val codec: String,
    val height: Int,
    val index: Int,
    val language: String,
    val name: String,
    val width: Int,
)
