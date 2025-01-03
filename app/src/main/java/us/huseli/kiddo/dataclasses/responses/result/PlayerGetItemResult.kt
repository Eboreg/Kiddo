package us.huseli.kiddo.dataclasses.responses.result

import com.google.gson.annotations.SerializedName

data class PlayerGetItemResult(
    val item: Item,
) {
    data class Item(
        // List.Item.All extends List.Item.Base
        val channel: String?,
        val channelnumber: Int?,
        val channeltype: PVRChannelType?,
        val endtime: String?,
        val hidden: Boolean?,
        val locked: Boolean?,
        val starttime: String?,
        val subchannelnumber: Int?,

        // List.Item.Base extends Video.Details.File, Audio.Details.Media
        val album: String?,
        val albumartist: List<String>?,
        val albumartistid: List<Int>?,
        val albumid: Int?,
        val albumlabel: String?,
        val albumreleasetype: AudioAlbumReleaseType?,
        val albumstatus: String?,
        val bitrate: Int?,
        val bpm: Int?,
        val cast: List<VideoCast>?,
        val channels: Int?,
        val comment: String?,
        val compilation: Boolean?,
        val contributors: List<AudioContributor>?,
        val country: List<String>?,
        val customproperties: Map<String, String>?,
        val description: String?,
        val disc: Int?,
        val disctitle: String?,
        val displaycomposer: String?,
        val displayconductor: String?,
        val displaylyricist: String?,
        val displayorchestra: String?,
        val duration: Int?,
        val dynpath: String?,
        val episode: Int?,
        val episodeguide: String?,
        val firstaired: String?,
        val id: Int?,
        val imdbnumber: String?,
        val isboxset: Boolean?,
        val lyrics: String?,
        val mediapath: String?,
        val mood: List<String>?,
        val mpaa: String?,
        val musicbrainzartistid: List<String>?,
        val musicbrainztrackid: String?,
        val originaldate: String?,
        val originaltitle: String?,
        val plotoutline: String?,
        val premiered: String?,
        val productioncode: String?,
        val releasedate: String?,
        val releasetype: AudioAlbumReleaseType?,
        val samplerate: Int?,
        val season: Int?,
        val set: String?,
        val setid: Int?,
        val showlink: List<String>?,
        val showtitle: String?,
        val songvideourl: String?,
        val sorttitle: String?,
        val specialsortepisode: Int?,
        val specialsortseason: Int?,
        val studio: List<String>?,
        val style: List<String>?,
        val tag: List<String>?,
        val tagline: String?,
        val theme: List<String>?,
        val top250: Int?,
        val totaldiscs: Int?,
        val track: Int?,
        val trailer: String?,
        val tvshowid: Int?,
        val type: ItemType,
        val uniqueid: String?,
        val votes: String?,
        val watchedepisodes: Int?,
        val writer: List<String>?,

        // Audio.Details.Media extends Audio.Details.Base
        val artist: List<String>?,
        val artistid: List<Int>?,
        val displayartist: String?,
        val musicbrainzalbumartistid: List<String>?,
        val rating: Double?,
        val sortartist: String?,
        val userrating: Int?,
        val year: Int?,

        // Audio.Details.Base extends Media.Details.Base
        val genre: List<String>?,

        // Video.Details.File extends Video.Details.Item
        val director: List<String>?,
        val resume: VideoResume?,
        val runtime: Int?,
        val streamdetails: VideoStreams?,

        // Video.Details.Item extends Video.Details.Media
        val dateadded: String?,
        val file: String?,
        val lastplayed: String?,
        val plot: String?,

        // Video.Details.Media extends Video.Details.Base
        val title: String?,

        // Video.Details.Base extends Media.Details.Base
        val art: MediaArtwork?,
        val playcount: Int?,

        // Media.Details.Base extends Item.Details.Base
        val fanart: String?,
        val thumbnail: String?,

        // Item.Details.Base
        val label: String,
    ) {
        @Suppress("unused")
        enum class ItemType {
            @SerializedName("channel") Channel,
            @SerializedName("episode") Episode,
            @SerializedName("movie") Movie,
            @SerializedName("musicvideo") MusicVideo,
            @SerializedName("picture") Picture,
            @SerializedName("recording") Recording,
            @SerializedName("song") Song,
            @SerializedName("unknown") Unknown,
        }

        @Suppress("unused")
        enum class PVRChannelType {
            @SerializedName("radio") Radio,
            @SerializedName("tv") Tv,
        }

        @Suppress("unused")
        enum class AudioAlbumReleaseType {
            @SerializedName("album") Album,
            @SerializedName("single") Single,
        }

        data class VideoCast(
            val name: String,
            val order: Int,
            val role: String,
            val thumbnail: String?,
        )

        data class AudioContributor(
            val artistid: Int,
            val name: String,
            val role: String,
            val roleid: String,
        )

        data class VideoResume(
            val position: Double?,
            val total: Double?,
        )

        data class VideoStreams(
            val audio: List<Audio>?,
            val subtitle: List<Subtitle>?,
            val video: List<Video>?,
        ) {
            data class Audio(
                val channels: Int?,
                val codec: String?,
                val language: String?,
            )

            data class Subtitle(
                val language: String?,
            )

            data class Video(
                val aspect: Double?,
                val codec: String?,
                val duration: Int?,
                val hdrtype: String?,
                val height: Int?,
                val width: Int?,
            )
        }

        data class MediaArtwork(
            val banner: String?,
            val fanart: String?,
            val poster: String?,
            val thumb: String?,
        )
    }
}
