package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.types.interfaces.IListFilter
import us.huseli.kiddo.takeIfNotEmpty

data class ListFilterAlbums(
    override val and: Collection<ListFilterAlbums>? = null,
    override val or: Collection<ListFilterAlbums>? = null,
    override val rule: ListFilterRule<ListFilterFieldsAlbums>? = null,
) : IListFilter<ListFilterAlbums, ListFilterRule<ListFilterFieldsAlbums>> {
    constructor(value: String, field: ListFilterFieldsAlbums) :
        this(and = null, or = null, rule = ListFilterRule(value = value, field = field))

    companion object {
        fun and(vararg filters: ListFilterAlbums?): ListFilterAlbums? =
            filters.filterNotNull().takeIfNotEmpty()?.let { ListFilterAlbums(and = it) }
    }
}
