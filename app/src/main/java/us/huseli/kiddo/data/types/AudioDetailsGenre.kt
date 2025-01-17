package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class AudioDetailsGenre(
    val genreid: Int,
    val title: String?,
)
