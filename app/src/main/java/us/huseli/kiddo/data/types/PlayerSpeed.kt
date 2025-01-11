package us.huseli.kiddo.data.types

import us.huseli.kiddo.data.interfaces.IHasPlayerSpeed

data class PlayerSpeed(
    override val speed: Int,
) : IHasPlayerSpeed
