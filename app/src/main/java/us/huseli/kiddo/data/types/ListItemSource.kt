package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.types.interfaces.IListItemSource

data class ListItemSource(
    override val file: String,
    override val label: String,
) : IListItemSource
