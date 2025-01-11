package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.interfaces.IListFilterRule

data class ListFilterRule<FieldType : Any>(
    override val operator: ListFilterOperators,
    override val value: List<String>,
    override val field: FieldType,
) : IListFilterRule<FieldType> {
    constructor(operator: ListFilterOperators = ListFilterOperators.Is, value: String, field: FieldType) :
        this(operator = operator, value = listOf(value), field = field)
}
