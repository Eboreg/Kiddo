package us.huseli.kiddo.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import us.huseli.kiddo.Repository
import us.huseli.retaintheme.utils.AbstractBaseViewModel
import us.huseli.retaintheme.utils.ILogger

abstract class AbstractSafeLoaderViewModel<T : Any> : AbstractBaseViewModel(), ILogger {
    abstract val repository: Repository

    private val _loading = MutableStateFlow(true)
    private val _exception = MutableStateFlow<Throwable?>(null)
    private val _data = MutableStateFlow<T?>(null)

    val exception = _exception.asStateFlow()
    val loading = _loading.asStateFlow()
    val data = _data.asStateFlow()

    abstract suspend fun getData(): T?

    protected suspend fun loadData() {
        try {
            _data.value = getData()
            _exception.value = null
        } catch (e: Throwable) {
            logError(exception = e)
        } finally {
            _loading.value = false
        }
    }
}
