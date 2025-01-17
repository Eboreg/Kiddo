package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.types.interfaces.IListItemSource

@Immutable
data class ListItemSource(
    override val file: String,
    override val label: String,
) : IListItemSource
