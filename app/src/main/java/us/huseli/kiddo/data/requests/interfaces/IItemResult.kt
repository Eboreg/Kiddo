package us.huseli.kiddo.data.requests.interfaces

import us.huseli.kiddo.data.types.interfaces.IItemDetailsBase

interface IItemResult<T : IItemDetailsBase> {
    val item: T?
}
