package us.huseli.kiddo.managers.download

import us.huseli.kiddo.formatFileSize
import us.huseli.kiddo.sensibleFormat
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class DownloadProgress(
    val fileSize: Long? = null,
    private val samples: List<Sample> = emptyList(),
) {
    val percent: Int?
        get() = getPercentage(fileSize, samples)
    val bps: Long?
        get() = samples.bps
    val remaining: Duration?
        get() = if (fileSize != null) {
            bps?.let { (fileSize - samples.last().written) / it }?.toDuration(DurationUnit.SECONDS)
        } else null
    val notificationText: String?
        get() = listOfNotNull(
            fileSize?.formatFileSize(0)?.let { "${samples.written.formatFileSize(0)}/$it" },
            bps?.formatFileSize()?.plus("/s"),
            remaining?.sensibleFormat()?.let { "ETA: $it" },
        ).takeIfNotEmpty()?.joinToString(" · ")

    fun addSample(written: Long): DownloadProgress {
        val sample = Sample(written = written)
        val newPercent = getPercentage(fileSize, samples + sample)

        if (newPercent != percent) return copy(samples = samples + sample)
        return this
    }

    override fun equals(other: Any?): Boolean =
        other is DownloadProgress && other.fileSize == fileSize && other.percent == percent

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (fileSize?.hashCode() ?: 0)
        result = 31 * result + (percent?.hashCode() ?: 0)
        return result
    }

    data class Sample(
        val written: Long,
        val timestamp: Long = System.currentTimeMillis(),
    )

    companion object {
        fun getPercentage(fileSize: Long?, samples: List<Sample>): Int? =
            fileSize?.takeIf { it > 0 }?.let { ((samples.written * 100f) / it) }?.toInt()?.coerceIn(0, 100)
    }
}

val List<DownloadProgress.Sample>.written: Long
    get() = maxOfOrNull { it.written } ?: 0L

val List<DownloadProgress.Sample>.bps: Long?
    get() = takeIf { it.size >= 5 }?.takeLast(5)?.let { samples ->
        val first = samples.first()
        val last = samples.last()
        val elapsedMs = last.timestamp - first.timestamp
        val written = last.written - first.written

        written / (elapsedMs / 1000)
    }
