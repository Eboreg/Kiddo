package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.interfaces.IListFilter
import us.huseli.kiddo.takeIfNotEmpty

data class ListFilterMovies(
    override val and: List<ListFilterMovies>? = null,
    override val or: List<ListFilterMovies>? = null,
    override val rule: ListFilterRule<ListFilterFieldsMovies>? = null,
) : IListFilter<ListFilterMovies, ListFilterRule<ListFilterFieldsMovies>> {
    constructor(value: String, field: ListFilterFieldsMovies, operator: ListFilterOperators = ListFilterOperators.Is) :
        this(and = null, or = null, rule = ListFilterRule(value = value, field = field, operator = operator))

    companion object {
        fun and(vararg filters: ListFilterMovies?): ListFilterMovies? = and(filters.toList())

        fun and(filters: Iterable<ListFilterMovies?>): ListFilterMovies? =
            filters.filterNotNull().takeIfNotEmpty()?.let { ListFilterMovies(and = it) }

        fun or(vararg filters: ListFilterMovies?): ListFilterMovies? = or(filters.toList())

        fun or(filters: Iterable<ListFilterMovies?>): ListFilterMovies? =
            filters.filterNotNull().takeIfNotEmpty()?.let { ListFilterMovies(or = it) }
    }
}
