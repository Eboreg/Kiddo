package us.huseli.kiddo.data.types.interfaces

import com.google.gson.annotations.SerializedName

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
}
