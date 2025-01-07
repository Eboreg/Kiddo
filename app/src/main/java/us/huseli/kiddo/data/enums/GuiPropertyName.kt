package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class GuiPropertyName {
    @SerializedName("currentwindow") CurrentWindow,
    @SerializedName("currentcontrol") CurrentControl,
    @SerializedName("skin") Skin,
    @SerializedName("fullscreen") Fullscreen,
    @SerializedName("stereoscopicmode") StereoscopicMode,
}
