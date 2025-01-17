package us.huseli.kiddo.managers.download

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.compose.runtime.Immutable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.yield
import us.huseli.kiddo.Constants.CANCEL_DOWNLOAD_INTENT_ACTION
import us.huseli.kiddo.getDisplayName
import us.huseli.retaintheme.extensions.pow
import us.huseli.retaintheme.request.Request
import us.huseli.retaintheme.utils.AbstractScopeHolder
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Immutable
data class DownloadData(
    val id: Int,
    val filename: String,
    val url: String,
    val localUri: Uri,
    val extra: Any? = null,
)

sealed class DownloadEvent {
    abstract val data: DownloadData

    data class Started(override val data: DownloadData, val cancelIntent: PendingIntent) : DownloadEvent()
    data class Cancelled(override val data: DownloadData) : DownloadEvent()
    data class Finished(override val data: DownloadData) : DownloadEvent()
    data class Progress(override val data: DownloadData, val progress: DownloadProgress) : DownloadEvent()
}

fun interface DownloadListener {
    fun onDownloadEvent(event: DownloadEvent)
}

@Singleton
class DownloadManager @Inject constructor(@ApplicationContext private val context: Context) : AbstractScopeHolder() {
    private val jobs = mutableMapOf<Int, Job>()
    private val listeners = mutableListOf<DownloadListener>()

    fun cancel(id: Int) {
        jobs[id]?.also { job ->
            launchOnIOThread {
                job.cancelAndJoin()
            }
            jobs.remove(id)
        }
    }

    fun downloadFile(url: String, localUri: Uri, originalPath: String, extra: Any? = null) {
        val id = Random.nextInt(2.pow(20))
        val job = launchOnIOThread {
            downloadFile(
                url = url,
                localUri = localUri,
                id = id,
                originalPath = originalPath,
                extra = extra,
            )
        }

        jobs.put(id, job)
    }

    fun registerListener(listener: DownloadListener) {
        listeners.add(listener)
    }

    fun unregisterListener(listener: DownloadListener) {
        listeners.remove(listener)
    }

    private fun createCancelIntent(id: Int): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, DownloadBroadcastReceiver::class.java).apply {
                action = CANCEL_DOWNLOAD_INTENT_ACTION
                putExtra("id", id)
            },
            PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private suspend fun CoroutineScope.downloadFile(
        url: String,
        localUri: Uri,
        id: Int,
        originalPath: String,
        extra: Any? = null,
    ) {
        val request = Request(url = url)

        request.getInputStream().use { inputStream ->
            val filename = localUri.getDisplayName(context) ?: originalPath.substringBeforeLast('/')
            val data = DownloadData(id = id, filename = filename, url = url, localUri = localUri, extra = extra)

            context.contentResolver.openOutputStream(data.localUri)?.use { outputStream ->
                val buf = ByteArray(8192)
                var chunkLength: Int
                var written: Long = 0
                var progress = DownloadProgress(fileSize = request.contentLength?.toLong())

                listeners.forEach { it.onDownloadEvent(DownloadEvent.Started(data, createCancelIntent(data.id))) }

                try {
                    while (inputStream.read(buf).also { chunkLength = it } > 0) {
                        outputStream.write(buf, 0, chunkLength)
                        written += chunkLength
                        yield()

                        progress.addSample(written).also {
                            if (it != progress) {
                                progress = it
                                listeners.forEach { it.onDownloadEvent(DownloadEvent.Progress(data, progress)) }
                            }
                        }
                    }
                } catch (e: Throwable) {
                    DocumentsContract.deleteDocument(context.contentResolver, data.localUri)
                    listeners.forEach { it.onDownloadEvent(DownloadEvent.Cancelled(data)) }
                    throw e
                }

                listeners.forEach { it.onDownloadEvent(DownloadEvent.Finished(data)) }
            }
        }
    }
}
