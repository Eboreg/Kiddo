package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class MediaArtwork(
    val banner: String?,
    val fanart: String?,
    val poster: String?,
    val thumb: String?,
)
