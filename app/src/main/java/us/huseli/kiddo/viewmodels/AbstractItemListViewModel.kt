package us.huseli.kiddo.viewmodels

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import us.huseli.kiddo.data.types.interfaces.IItemDetailsBase
import us.huseli.kiddo.paging.MediaPagingSource
import us.huseli.retaintheme.utils.AbstractBaseViewModel

abstract class AbstractItemListViewModel<T : IItemDetailsBase> : AbstractBaseViewModel() {
    abstract val pagingSourceFactory: () -> MediaPagingSource<T>

    private var _pagingSource: MediaPagingSource<T>? = null

    val pager = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = { pagingSourceFactory().also { _pagingSource = it } },
    ).flow.cachedIn(viewModelScope)

    fun invalidateSource() {
        _pagingSource?.invalidate()
    }

    override fun onCleared() {
        super.onCleared()
        _pagingSource = null
    }
}
