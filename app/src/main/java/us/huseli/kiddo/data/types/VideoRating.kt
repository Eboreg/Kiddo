package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class VideoRating(
    val default: Boolean?,
    val rating: Double,
    val votes: Int?,
)
