package us.huseli.kiddo.data.enums

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Image
import androidx.compose.material.icons.sharp.MusicNote
import androidx.compose.material.icons.sharp.MusicVideo
import androidx.compose.material.icons.sharp.QuestionMark
import androidx.compose.material.icons.sharp.Videocam
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.R

@Suppress("unused")
enum class PlaylistType(val resId: Int, val icon: ImageVector) {
    @SerializedName("audio") Audio(R.string.audio, Icons.Sharp.MusicNote),
    @SerializedName("mixed") Mixed(R.string.mixed, Icons.Sharp.MusicVideo),
    @SerializedName("picture") Picture(R.string.picture, Icons.Sharp.Image),
    @SerializedName("unknown") Unknown(R.string.unknown, Icons.Sharp.QuestionMark),
    @SerializedName("video") Video(R.string.video, Icons.Sharp.Videocam),
}
