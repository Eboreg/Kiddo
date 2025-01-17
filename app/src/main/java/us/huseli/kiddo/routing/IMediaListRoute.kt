package us.huseli.kiddo.routing

import us.huseli.kiddo.data.enums.ListFilterOperators
import us.huseli.kiddo.data.types.ListFilter
import us.huseli.kiddo.data.types.ListSort
import us.huseli.retaintheme.extensions.takeIfNotBlank

interface IMediaListRoute<Field : Any> {
    val descending: Boolean?
    val freetext: String?
    val freetextFiltersFields: List<Field>
    val hasFilters: Boolean
    val sortMethod: ListSort.Method?

    val filter: ListFilter<Field>?
        get() {
            val filters = getFilters()
            val freetextFilter = freetext?.takeIfNotBlank()?.let { freetext ->
                ListFilter.or(freetextFiltersFields.map { ListFilter(freetext, it, ListFilterOperators.Contains) })
            }

            return ListFilter.and(
                ListFilter.and(*filters.toTypedArray()),
                freetextFilter,
            )
        }

    val hasSearch: Boolean
        get() = freetext?.takeIfNotBlank() != null

    val listSort: ListSort?
        get() = sortMethod?.let {
            ListSort(
                method = it,
                order = if (descending == true) ListSort.Order.Descending else ListSort.Order.Ascending,
            )
        }

    fun getFilters(): List<ListFilter<Field>>
}
