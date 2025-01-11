package us.huseli.kiddo.viewmodels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import us.huseli.kiddo.data.types.interfaces.IItemDetailsBase
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import us.huseli.retaintheme.utils.ILogger

abstract class AbstractItemListViewModel<Item : IItemDetailsBase>() : AbstractBaseViewModel(), ILogger {
    private val _loading = MutableStateFlow(true)
    private val _exception = MutableStateFlow<Throwable?>(null)

    val exception = _exception.asStateFlow()
    val loading = _loading.asStateFlow()
    val items: StateFlow<List<Item>>
        get() = getItemsFlow()
            .catch { e ->
                _exception.value = e
                logError("getItemsFlow()", e)
            }
            .onEach {
                _loading.value = false
                _exception.value = null
            }
            .stateWhileSubscribed(emptyList())

    abstract fun getItemsFlow(): Flow<List<Item>>
}
