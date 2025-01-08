package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.ListFilterFieldsAlbums
import us.huseli.kiddo.data.types.interfaces.IListFilter

data class ListFilterAlbums(
    override val and: List<ListFilterAlbums>? = null,
    override val or: List<ListFilterAlbums>? = null,
    override val rule: ListFilterRuleAlbums? = null,
) : IListFilter<ListFilterAlbums, ListFilterRuleAlbums> {
    constructor(value: String, field: ListFilterFieldsAlbums) :
        this(and = null, or = null, rule = ListFilterRuleAlbums(value = value, field = field))
}
