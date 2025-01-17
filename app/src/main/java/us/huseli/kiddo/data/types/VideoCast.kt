package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class VideoCast(
    val name: String,
    val order: Int,
    val role: String,
    val thumbnail: String?,
)
