package us.huseli.kiddo.dataclasses.responses.result

import com.google.gson.annotations.SerializedName

data class GetActivePlayersResult(
    val playerid: Int,
    val playertype: PlayerPlayerType,
    val type: PlayerType,
) {
    @Suppress("unused")
    enum class PlayerPlayerType {
        @SerializedName("internal") Internal,
        @SerializedName("external") External,
        @SerializedName("remote") Remote,
    }

    @Suppress("unused")
    enum class PlayerType {
        @SerializedName("video") Video,
        @SerializedName("audio") Audio,
        @SerializedName("picture") Picture,
    }
}
