package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.retaintheme.extensions.takeIfNotEmpty

class ListFilter<Field : Any>(
    val and: Collection<ListFilter<Field>>? = null,
    val or: Collection<ListFilter<Field>>? = null,
    val rule: ListFilterRule<Field>? = null,
) {
    constructor(value: String, field: Field, operator: ListFilterOperators = ListFilterOperators.Is) :
        this(and = null, or = null, rule = ListFilterRule(value = value, field = field, operator = operator))

    fun getParams(): Map<String, Any> {
        val argCount = listOfNotNull(and, or, rule).size

        require(argCount == 1)
        return and?.let { mapOf("and" to it.map { it.getParams() }) }
            ?: or?.let { mapOf("or" to it.map { it.getParams() }) }
            ?: rule!!.getParams()
    }

    companion object {
        fun <Field : Any> and(vararg filters: ListFilter<Field>?): ListFilter<Field>? =
            and(filters.toList())

        fun <Field : Any> and(filters: Iterable<ListFilter<Field>?>?): ListFilter<Field>? =
            filters?.filterNotNull()?.takeIfNotEmpty()?.let { if (it.size == 1) it.first() else ListFilter(and = it) }

        @Suppress("unused")
        fun <Field : Any> or(vararg filters: ListFilter<Field>?): ListFilter<Field>? =
            or(filters.toList())

        fun <Field : Any> or(filters: Iterable<ListFilter<Field>?>?): ListFilter<Field>? =
            filters?.filterNotNull()?.takeIfNotEmpty()?.let { if (it.size == 1) it.first() else ListFilter(or = it) }
    }
}
