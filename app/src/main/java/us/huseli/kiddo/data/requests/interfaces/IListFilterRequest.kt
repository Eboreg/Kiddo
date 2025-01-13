package us.huseli.kiddo.data.requests.interfaces

import us.huseli.kiddo.data.types.ListFilter
import us.huseli.retaintheme.extensions.takeIfNotEmpty

interface IListFilterRequest<Filter : ListFilter<*>, SimpleFilter : ISimpleFilter> {
    val filter: Filter?
    val simpleFilter: SimpleFilter?

    fun getFilterParams(): Map<String, Any>? {
        return listOfNotNull(filter?.getParams(), simpleFilter?.getParams())
            .takeIfNotEmpty()
            ?.reduce { acc, m -> acc + m }
            ?.takeIfNotEmpty()
    }
}
