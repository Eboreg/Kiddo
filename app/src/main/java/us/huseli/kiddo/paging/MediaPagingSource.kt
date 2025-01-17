package us.huseli.kiddo.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import us.huseli.kiddo.data.requests.interfaces.IListResult
import us.huseli.kiddo.data.types.ListLimits
import us.huseli.kiddo.data.types.interfaces.IItemDetailsBase
import us.huseli.retaintheme.utils.ILogger

open class MediaPagingSource<Value : IItemDetailsBase>(
    val requester: suspend (ListLimits) -> IListResult<Value>?,
) : PagingSource<ListLimits, Value>(), ILogger {
    override fun getRefreshKey(state: PagingState<ListLimits, Value>): ListLimits? {
        val anchorPosition = state.anchorPosition ?: return null
        val start = (anchorPosition - (state.config.pageSize / 2)).coerceAtLeast(0)

        return ListLimits(start = start, end = start + state.config.pageSize)
    }

    override suspend fun load(params: LoadParams<ListLimits>): LoadResult<ListLimits, Value> {
        val start = params.key ?: ListLimits(start = 0, end = params.loadSize)
        val result = requester(start)
        val items = result?.items

        return if (result != null && items != null) {
            val nextKey = result.limits.end?.takeIf { it < result.limits.total }?.let { end ->
                ListLimits(start = end, end = (end + params.loadSize).coerceAtMost(result.limits.total))
            }
            val prevKey = start.start?.takeIf { it > 0 }?.let { start ->
                ListLimits(start = (start - params.loadSize).coerceAtLeast(0), end = start)
            }

            LoadResult.Page(data = items, prevKey = prevKey, nextKey = nextKey)
        } else LoadResult.Error(Exception("null returned"))
    }
}
