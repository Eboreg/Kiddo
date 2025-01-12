package us.huseli.kiddo.data.types.interfaces

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.sensibleFormat
import us.huseli.retaintheme.extensions.takeIfNotEmpty
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

interface IListItemAll : IListItemBase {
    val channel: String?
    val channelnumber: Int?
    val channeltype: PVRChannelType?
    val endtime: String?
    val hidden: Boolean?
    val locked: Boolean?
    val starttime: String?
    val subchannelnumber: Int?

    @Suppress("unused")
    enum class PVRChannelType {
        @SerializedName("radio") Radio,
        @SerializedName("tv") Tv,
    }

    val stringId: String?
        get() = id?.toString()
            ?: file?.takeIfNotEmpty()
            ?: (customproperties?.get("video_id") as? String)?.takeIfNotEmpty()
            ?: (customproperties?.get("original_listitem_url") as? String)?.takeIfNotEmpty()

    val stringIdForced: String
        get() = stringId ?: UUID.randomUUID().toString()

    val supportingContent: String?
        get() = listOfNotNull(
            artistString,
            directorString,
            runtime?.seconds?.sensibleFormat(),
            year?.takeIf { it > 0 }?.toString(),
        ).takeIfNotEmpty()?.joinToString(" · ")
}
