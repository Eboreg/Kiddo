package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class PlayerPositionTime(
    val hours: Int? = null,
    val milliseconds: Int? = null,
    val minutes: Int? = null,
    val seconds: Int? = null,
)
