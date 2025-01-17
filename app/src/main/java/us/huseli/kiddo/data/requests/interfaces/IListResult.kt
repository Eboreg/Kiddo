package us.huseli.kiddo.data.requests.interfaces

import us.huseli.kiddo.data.types.ListLimitsReturned
import us.huseli.kiddo.data.types.interfaces.IItemDetailsBase

interface IListResult<T : IItemDetailsBase> {
    val items: List<T>?
    val limits: ListLimitsReturned
}
