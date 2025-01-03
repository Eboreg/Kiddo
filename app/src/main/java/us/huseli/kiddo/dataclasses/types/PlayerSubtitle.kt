package us.huseli.kiddo.dataclasses.types

data class PlayerSubtitle(
    val index: Int,
    val isdefault: Boolean,
    val isforced: Boolean,
    val isimpaired: Boolean,
    val language: String,
    val name: String,
)
