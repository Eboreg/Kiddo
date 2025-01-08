package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.enums.ListFilterOperators

interface IListFilterRule<FieldType : Any> {
    val operator: ListFilterOperators
    val value: List<String>
    val field: FieldType

    fun getParams(): Map<String, Any> = mapOf(
        "operator" to operator,
        "value" to value,
        "field" to field,
    )
}
