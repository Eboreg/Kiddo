package us.huseli.kiddo

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow

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

    suspend fun <Result> run(
        method: String,
        requestId: Int,
        minIntervalMs: Long,
        callback: suspend () -> Result,
    ): Result? {
        val now = System.currentTimeMillis()
        val req = RateLimitedRequest(method = method, requestId = requestId)

        cancelPending(method)
        _requests.value += req

        val lastRun = _requests.value
            .filter { it.method == method && it.isActiveOrFinished() }
            .filter { now - it.startTime < minIntervalMs }
            .maxByOrNull { it.startTime }
        val startDelay = lastRun?.let { minIntervalMs - (now - it.startTime) }?.coerceIn(0L, minIntervalMs)

        return req.run<Result>(startDelay, callback)
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
