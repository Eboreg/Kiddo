package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class ListLimitsReturned(
    val end: Int?,
    val start: Int?,
    val total: Int,
)
