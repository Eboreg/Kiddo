package us.huseli.kiddo.viewmodels

sealed class ItemLoadState<T : Any>() {
    abstract val item: T?

    class Loading<T : Any>() : ItemLoadState<T>() {
        override val item: T? = null
    }

    class Error<T : Any>(val error: Throwable) : ItemLoadState<T>() {
        override val item: T? = null
    }

    class Loaded<T : Any>(override val item: T) : ItemLoadState<T>()
}
