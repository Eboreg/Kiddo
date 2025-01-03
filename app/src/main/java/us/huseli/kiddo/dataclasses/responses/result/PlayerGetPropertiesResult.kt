package us.huseli.kiddo.dataclasses.responses.result

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.dataclasses.types.GlobalTime
import us.huseli.kiddo.dataclasses.types.PlayerAudioStream
import us.huseli.kiddo.dataclasses.types.PlayerSubtitle
import us.huseli.kiddo.dataclasses.types.PlayerVideoStream

data class PlayerGetPropertiesResult(
    val audiostreams: List<PlayerAudioStream>?,
    val cachepercentage: Double?,
    val canchangespeed: Boolean?,
    val canmove: Boolean?,
    val canrepeat: Boolean?,
    val canrotate: Boolean?,
    val canseek: Boolean?,
    val canshuffle: Boolean?,
    val canzoom: Boolean?,
    val currentaudiostream: PlayerAudioStream?,
    val currentsubtitle: PlayerSubtitle?,
    val currentvideostream: PlayerVideoStream?,
    val live: Boolean?,
    val partymode: Boolean?,
    val percentage: Double?,
    val playlistid: Int?,
    val position: Int?,
    val repeat: PlayerRepeat?,
    val shuffled: Boolean?,
    val speed: Double?,
    val subtitleenabled: Boolean?,
    val subtitles: List<PlayerSubtitle>?,
    val time: GlobalTime?,
    val totaltime: GlobalTime?,
    val type: String,
    val videostreams: List<PlayerVideoStream>?,
) {
    @Suppress("unused")
    enum class PlayerRepeat {
        @SerializedName("off") Off,
        @SerializedName("one") One,
        @SerializedName("all") All,
    }
}
