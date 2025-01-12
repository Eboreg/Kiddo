package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.interfaces.IListFilter
import us.huseli.retaintheme.extensions.takeIfNotEmpty

data class ListFilterAlbums(
    override val and: Collection<ListFilterAlbums>? = null,
    override val or: Collection<ListFilterAlbums>? = null,
    override val rule: ListFilterRule<ListFilterFieldsAlbums>? = null,
) : IListFilter<ListFilterAlbums, ListFilterRule<ListFilterFieldsAlbums>> {
    constructor(value: String, field: ListFilterFieldsAlbums, operator: ListFilterOperators = ListFilterOperators.Is) :
        this(and = null, or = null, rule = ListFilterRule(value = value, field = field, operator = operator))

    companion object {
        fun and(vararg filters: ListFilterAlbums?): ListFilterAlbums? = and(filters.toList())

        fun and(filters: Iterable<ListFilterAlbums?>): ListFilterAlbums? =
            filters.filterNotNull().takeIfNotEmpty()?.let { ListFilterAlbums(and = it) }

        fun or(vararg filters: ListFilterAlbums?): ListFilterAlbums? = or(filters.toList())

        fun or(filters: Iterable<ListFilterAlbums?>): ListFilterAlbums? =
            filters.filterNotNull().takeIfNotEmpty()?.let { ListFilterAlbums(or = it) }
    }
}
