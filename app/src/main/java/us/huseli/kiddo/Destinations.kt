package us.huseli.kiddo

import androidx.navigation.NamedNavArgument

interface IDestination {
    val routeTemplate: String
    val arguments: List<NamedNavArgument>
}

abstract class AbstractSimpleDestination(override val routeTemplate: String) : IDestination {
    override val arguments: List<NamedNavArgument> = emptyList()
    fun route() = routeTemplate
}

object RemoteControlDestination : AbstractSimpleDestination("remote-control")

object SettingsDestination : AbstractSimpleDestination("settings")
