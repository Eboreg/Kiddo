package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.interfaces.IListFilterRule

data class ListFilterRuleAlbums(
    override val operator: ListFilterOperators,
    override val value: List<String>,
    override val field: ListFilterFieldsAlbums,
) : IListFilterRule<ListFilterFieldsAlbums> {
    constructor(operator: ListFilterOperators = ListFilterOperators.Is, value: String, field: ListFilterFieldsAlbums) :
        this(operator = operator, value = listOf(value), field = field)
}
