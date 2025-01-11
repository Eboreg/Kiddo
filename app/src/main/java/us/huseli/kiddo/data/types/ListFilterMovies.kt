package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.types.interfaces.IListFilter
import us.huseli.kiddo.takeIfNotEmpty

data class ListFilterMovies(
    override val and: List<ListFilterMovies>? = null,
    override val or: List<ListFilterMovies>? = null,
    override val rule: ListFilterRule<ListFilterFieldsMovies>? = null,
) : IListFilter<ListFilterMovies, ListFilterRule<ListFilterFieldsMovies>> {
    constructor(value: String, field: ListFilterFieldsMovies) :
        this(and = null, or = null, rule = ListFilterRule(value = value, field = field))

    companion object {
        fun and(vararg filters: ListFilterMovies?): ListFilterMovies? =
            filters.filterNotNull().takeIfNotEmpty()?.let { ListFilterMovies(and = it) }
    }
}
