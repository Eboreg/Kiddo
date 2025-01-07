package us.huseli.kiddo.data.notifications.data

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.notifications.interfaces.INotificationData

data class InputOnInputRequested(
    val title: String,
    val type: Type,
    val value: String,
) : INotificationData {
    @Suppress("unused")
    enum class Type {
        @SerializedName("keyboard") Keyboard,
        @SerializedName("time") Time,
        @SerializedName("date") Date,
        @SerializedName("ip") Ip,
        @SerializedName("password") Password,
        @SerializedName("numericpassword") NumericPassword,
        @SerializedName("number") Number,
        @SerializedName("seconds") Seconds,
    }
}
