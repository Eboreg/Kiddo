package us.huseli.kiddo.data.types

import us.huseli.kiddo.stripTags
import us.huseli.retaintheme.extensions.takeIfNotBlank

data class PlayerSubtitle(
    val index: Int,
    val isdefault: Boolean,
    val isforced: Boolean,
    val isimpaired: Boolean,
    val language: String,
    val name: String,
) {
    val displayName: String
        get() = name.stripTags() + (language.takeIfNotBlank()?.let { " ($it)" } ?: "")
}
