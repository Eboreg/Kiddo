package us.huseli.kiddo.data.types

import us.huseli.kiddo.AbstractListMembers
import us.huseli.kiddo.data.enums.PlayerRepeat
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed
import us.huseli.kiddo.data.interfaces.IHasPlayerTime
import us.huseli.kiddo.data.interfaces.IHasPlayerTotalTime
import us.huseli.kiddo.data.interfaces.IHasPlaylistId

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
    override val playlistid: Int?,
    val position: Int?,
    val repeat: PlayerRepeat?,
    val shuffled: Boolean?,
    override val speed: Int?,
    val subtitleenabled: Boolean?,
    val subtitles: List<PlayerSubtitle>?,
    override val time: GlobalTime?,
    override val totaltime: GlobalTime?,
    val type: String,
    val videostreams: List<PlayerVideoStream>?,
) : IHasPlayerSpeed, IHasPlaylistId, IHasPlayerTotalTime, IHasPlayerTime, AbstractListMembers() {
    fun merge(other: PlayerPropertyValue) = copy(
        audiostreams = other.audiostreams ?: audiostreams,
        cachepercentage = other.cachepercentage ?: cachepercentage,
        canchangespeed = other.canchangespeed ?: canchangespeed,
        canmove = other.canmove ?: canmove,
        canrepeat = other.canrepeat ?: canrepeat,
        canrotate = other.canrotate ?: canrotate,
        canseek = other.canseek ?: canseek,
        canshuffle = other.canshuffle ?: canshuffle,
        canzoom = other.canzoom ?: canzoom,
        currentaudiostream = other.currentaudiostream ?: currentaudiostream,
        currentsubtitle = other.currentsubtitle ?: currentsubtitle,
        currentvideostream = other.currentvideostream ?: currentvideostream,
        live = other.live ?: live,
        partymode = other.partymode ?: partymode,
        percentage = other.percentage ?: percentage,
        playlistid = other.playlistid ?: playlistid,
        position = other.position ?: position,
        repeat = other.repeat ?: repeat,
        shuffled = other.shuffled ?: shuffled,
        speed = other.speed ?: speed,
        subtitleenabled = other.subtitleenabled ?: subtitleenabled,
        subtitles = other.subtitles ?: subtitles,
        time = other.time ?: time,
        totaltime = other.totaltime ?: totaltime,
        videostreams = other.videostreams ?: videostreams,
    )

    override fun toString(): String = memberPropertiesToString()
}
