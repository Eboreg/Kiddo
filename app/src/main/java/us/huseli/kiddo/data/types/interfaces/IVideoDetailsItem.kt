package us.huseli.kiddo.data.types.interfaces

interface IVideoDetailsItem : IVideoDetailsMedia {
    val dateadded: String?
    val file: String?
    val lastplayed: String?
    val plot: String?
}
