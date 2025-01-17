package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import com.google.gson.annotations.SerializedName

@Immutable
data class GuiPropertyValue(
    val currentcontrol: CurrentControl?,
    val currentwindow: CurrentWindow?,
    val fullscreen: Boolean?,
    val skin: Skin?,
    val stereoscopicmode: StereoscopyMode?,
) {
    @Immutable
    data class CurrentControl(
        val label: String,
    )

    @Immutable
    data class CurrentWindow(
        val id: Int,
        val label: String,
    )

    @Immutable
    data class Skin(
        val id: String,
        val name: String?,
    )

    @Immutable
    data class StereoscopyMode(
        val label: String,
        val mode: Mode,
    ) {
        @Suppress("unused")
        enum class Mode {
            @SerializedName("off") Off,
            @SerializedName("split_vertical") SplitVertical,
            @SerializedName("split_horizontal") SplitHorizontal,
            @SerializedName("row_interleaved") RowInterleaved,
            @SerializedName("hardware_based") HardwareBased,
            @SerializedName("anaglyph_cyan_red") AnaglyphCyanRed,
            @SerializedName("anaglyph_green_magenta") AnaglyphGreenMagenta,
            @SerializedName("anaglyph_yellow_blue") AnaglyphYellowBlue,
            @SerializedName("monoscopic") Monoscopic,
        }
    }
}
