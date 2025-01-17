package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.requests.interfaces.IRequestProperty

@Suppress("unused")
enum class GuiPropertyName : IRequestProperty {
    @SerializedName("currentwindow") CurrentWindow,
    @SerializedName("currentcontrol") CurrentControl,
    @SerializedName("skin") Skin,
    @SerializedName("fullscreen") Fullscreen,
    @SerializedName("stereoscopicmode") StereoscopicMode,
}
