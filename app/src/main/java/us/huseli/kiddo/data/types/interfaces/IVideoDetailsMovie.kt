package us.huseli.kiddo.data.types.interfaces

import us.huseli.kiddo.data.types.VideoRating

interface IVideoDetailsMovie : IVideoDetailsFile {
    val cast: List<IListItemBase.VideoCast>?
    val country: List<String>?
    val genre: List<String>?
    val imdbnumber: String?
    val movieid: Int
    val mpaa: String?
    val originaltitle: String?
    val plotoutline: String?
    val premiered: String?
    val rating: Double?
    val ratings: Map<String, VideoRating>?
    val set: String?
    val setid: Int?
    val showlink: List<String>?
    val sorttitle: String?
    val studio: List<String>?
    val tag: List<String>?
    val tagline: String?
    val top250: Int?
    val trailer: String?
    val uniqueid: String?
    val userrating: Int?
    val votes: String?
    val writer: List<String>?
    val year: Int?
}
