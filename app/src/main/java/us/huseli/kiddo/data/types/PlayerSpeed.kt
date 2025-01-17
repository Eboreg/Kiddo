package us.huseli.kiddo.data.types

import androidx.compose.runtime.Immutable
import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed

@Immutable
data class PlayerSpeed(
    override val speed: Int,
) : IHasPlayerSpeed
