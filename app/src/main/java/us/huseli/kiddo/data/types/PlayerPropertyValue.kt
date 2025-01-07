package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.enums.PlayerRepeat
import us.huseli.kiddo.data.notifications.interfaces.IHasPlayerSpeed

data class PlayerPropertyValue(
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
    override val speed: Int?,
    val subtitleenabled: Boolean?,
    val subtitles: List<PlayerSubtitle>?,
    val time: GlobalTime?,
    val totaltime: GlobalTime?,
    val type: String,
    val videostreams: List<PlayerVideoStream>?,
) : IHasPlayerSpeed
