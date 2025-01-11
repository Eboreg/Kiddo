package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsSongs
import us.huseli.kiddo.data.types.interfaces.IListFilter
import us.huseli.kiddo.takeIfNotEmpty

data class ListFilterSongs(
    override val and: Collection<ListFilterSongs>? = null,
    override val or: Collection<ListFilterSongs>? = null,
    override val rule: ListFilterRule<ListFilterFieldsSongs>? = null,
) : IListFilter<ListFilterSongs, ListFilterRule<ListFilterFieldsSongs>> {
    constructor(value: String, field: ListFilterFieldsSongs) :
        this(and = null, or = null, rule = ListFilterRule(value = value, field = field))

    companion object {
        fun and(vararg filters: ListFilterSongs?): ListFilterSongs? =
            filters.filterNotNull().takeIfNotEmpty()?.let { ListFilterSongs(and = it) }
    }
}
