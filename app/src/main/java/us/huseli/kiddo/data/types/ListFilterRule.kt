package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.enums.ListFilterOperators

@Immutable
data class ListFilterRule<FieldType : Any>(
    val operator: ListFilterOperators,
    val value: List<String>,
    val field: FieldType,
) {
    constructor(operator: ListFilterOperators = ListFilterOperators.Is, value: String, field: FieldType) :
        this(operator = operator, value = listOf(value), field = field)

    fun getParams(): Map<String, Any> = mapOf(
        "operator" to operator,
        "value" to value,
        "field" to field,
    )
}
