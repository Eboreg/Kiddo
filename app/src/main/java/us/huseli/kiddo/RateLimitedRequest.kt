package us.huseli.kiddo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import us.huseli.kiddo.data.AbstractRequest

class RateLimitedRequestManager {
    private val _requests = MutableStateFlow<List<RateLimitedRequest>>(emptyList())

    fun cancelPending(method: String) {
        _requests.value = _requests.value.toMutableList().apply {
            val pending = filter { it.method == method && it.isPending() }

            if (pending.isNotEmpty()) {
                removeAll(pending)
                pending.forEach { it.cancel() }
            }
        }
    }

    suspend fun <Result : Any, Request : AbstractRequest<Result>> run(
        request: Request,
        requestId: Int,
        minIntervalMs: Long,
        callback: suspend () -> Request
    ): Request? {
        val now = System.currentTimeMillis()
        val req = RateLimitedRequest(method = request.method, requestId = requestId)

        cancelPending(request.method)
        _requests.value += req

        val lastRun = _requests.value
            .filter { it.method == request.method && it.isActiveOrFinished() }
            .filter { now - it.startTime < minIntervalMs }
            .maxByOrNull { it.startTime }
        val startDelay = lastRun?.let { minIntervalMs - (now - it.startTime) }?.coerceIn(0L, minIntervalMs)

        return req.run<Request>(startDelay, callback)
    }
}

data class RateLimitedRequest(val method: String, val requestId: Int) {
    var status = Status.Pending
        private set

    var startTime = 0L
        private set

    enum class Status { Pending, Active, Finished, Cancelled }

    fun cancel() {
        status = Status.Cancelled
    }

    fun isActiveOrFinished() = status == Status.Finished || status == Status.Active

    fun isPending() = status == Status.Pending

    suspend fun <Result> run(startDelay: Long?, callback: suspend () -> Result?): Result? {
        startDelay?.also { delay(it) }

        if (status != Status.Cancelled) {
            status = Status.Active
            startTime = System.currentTimeMillis()

            return callback().also {
                status = Status.Finished
            }
        }
        return null
    }
}
