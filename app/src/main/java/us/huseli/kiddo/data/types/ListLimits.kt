package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class ListLimits(
    val end: Int?,
    val start: Int?,
)
