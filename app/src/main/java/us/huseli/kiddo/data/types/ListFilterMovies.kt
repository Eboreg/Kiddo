package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.types.interfaces.IListFilter

data class ListFilterMovies(
    override val and: List<ListFilterMovies>? = null,
    override val or: List<ListFilterMovies>? = null,
    override val rule: ListFilterRuleMovies? = null,
) : IListFilter<ListFilterMovies, ListFilterRuleMovies> {
    constructor(value: String, field: ListFilterFieldsMovies) :
        this(and = null, or = null, rule = ListFilterRuleMovies(value = value, field = field))
}
