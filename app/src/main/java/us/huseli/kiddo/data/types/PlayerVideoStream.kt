package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerVideoStream(
    val codec: String,
    val height: Int,
    val index: Int,
    val language: String,
    val name: String,
    val width: Int,
)
