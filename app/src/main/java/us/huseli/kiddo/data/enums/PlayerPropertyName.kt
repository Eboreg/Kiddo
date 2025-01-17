package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName
import us.huseli.kiddo.data.requests.interfaces.IRequestProperty

@Suppress("unused")
enum class PlayerPropertyName : IRequestProperty {
    @SerializedName("audiostreams") AudioStreams,
    @SerializedName("cachepercentage") CachePercentage,
    @SerializedName("canchangespeed") CanChangeSpeed,
    @SerializedName("canmove") CanMove,
    @SerializedName("canrepeat") CanRepeat,
    @SerializedName("canrotate") CanRotate,
    @SerializedName("canseek") CanSeek,
    @SerializedName("canshuffle") CanShuffle,
    @SerializedName("canzoom") CanZoom,
    @SerializedName("currentaudiostream") CurrentAudioStream,
    @SerializedName("currentsubtitle") CurrentSubtitle,
    @SerializedName("currentvideostream") CurrentVideoStream,
    @SerializedName("live") Live,
    @SerializedName("partymode") PartyMode,
    @SerializedName("percentage") Percentage,
    @SerializedName("playlistid") PlaylistId,
    @SerializedName("position") Position,
    @SerializedName("repeat") Repeat,
    @SerializedName("shuffled") Shuffled,
    @SerializedName("speed") Speed,
    @SerializedName("subtitleenabled") SubtitleEnabled,
    @SerializedName("subtitles") Subtitles,
    @SerializedName("time") Time,
    @SerializedName("totaltime") TotalTime,
    @SerializedName("type") Type,
    @SerializedName("videostreams") VideoStreams,
}
