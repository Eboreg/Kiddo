package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsMovies
import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.interfaces.IListFilterRule

data class ListFilterRuleMovies(
    override val operator: ListFilterOperators,
    override val value: List<String>,
    override val field: ListFilterFieldsMovies
) : IListFilterRule<ListFilterFieldsMovies> {
    constructor(operator: ListFilterOperators = ListFilterOperators.Is, value: String, field: ListFilterFieldsMovies) :
        this(operator = operator, value = listOf(value), field = field)
}
