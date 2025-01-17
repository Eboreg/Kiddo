package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable

@Immutable
data class AudioContributor(
    val artistid: Int,
    val name: String,
    val role: String,
    val roleid: String,
)
