package us.huseli.kiddo

import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

abstract class AbstractListMembers {
    open fun listNonNullProperties(): List<Pair<String, Any?>> {
        return this::class.memberProperties.mapNotNull { prop ->
            val value = (prop as KProperty1<AbstractListMembers, *>).get(this)
            value?.let { prop.name to it }
        }
    }

    fun nonNullPropertiesToString(): String {
        val props = listNonNullProperties().joinToString(", ") { (k, v) -> "$k=$v" }
        return "${javaClass.simpleName}($props)"
    }
}
