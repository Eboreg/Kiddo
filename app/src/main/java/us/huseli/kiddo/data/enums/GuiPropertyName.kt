package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.requests.interfaces.IRequestProperty

@Suppress("unused")
enum class GuiPropertyName : IRequestProperty {
    @SerializedName("currentcontrol") CurrentControl,
    @SerializedName("currentwindow") CurrentWindow,
    @SerializedName("fullscreen") Fullscreen,
    @SerializedName("skin") Skin,
    @SerializedName("stereoscopicmode") StereoscopicMode,
}
