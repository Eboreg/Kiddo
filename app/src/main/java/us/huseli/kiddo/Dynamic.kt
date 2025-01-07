package us.huseli.kiddo

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class Dynamic<T>(data: T? = null) {
    val status: Status by derivedStateOf {
        if (this.data != null) Status.Loaded
        else if (error != null) Status.Error
        else Status.Loading
    }
    var data: T? by mutableStateOf(data)
    var error: String? by mutableStateOf(null)

    enum class Status {
        Loading,
        Loaded,
        Error,
    }
}
