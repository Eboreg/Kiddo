package us.huseli.kiddo.viewmodels.loaders

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.huseli.kiddo.viewmodels.ItemLoadState
import us.huseli.retaintheme.utils.AbstractScopeHolder

abstract class AbstractItemStateLoader<T : Any> : AbstractScopeHolder() {
    private val _item = MutableStateFlow<ItemLoadState<T>>(ItemLoadState.Loading())
    val item = _item.asStateFlow()

    abstract suspend fun getter(): T?

    fun refresh() {
        launchOnIOThread {
            try {
                getter()?.also { _item.value = ItemLoadState.Loaded(it) }
            } catch (e: Throwable) {
                _item.value = ItemLoadState.Error(e)
            }
        }
    }
}
