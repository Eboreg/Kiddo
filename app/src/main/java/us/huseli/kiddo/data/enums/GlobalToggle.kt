package us.huseli.kiddo.data.enums

import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

enum class GlobalToggle(val value: Any) {
    True(true),
    False(false),
    Toggle("toggle");

    class TypeAdapter : JsonSerializer<GlobalToggle> {
        override fun serialize(
            src: GlobalToggle?,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement? {
            return when (src) {
                True -> JsonPrimitive(true)
                False -> JsonPrimitive(false)
                Toggle -> JsonPrimitive("toggle")
                null -> JsonNull.INSTANCE
            }
        }
    }

    companion object {
        fun fromValue(value: Any): GlobalToggle {
            if (value == true) return True
            if (value == false) return False
            if (value == "toggle") return Toggle
            throw Exception("Invalid GlobalToggle value: $value")
        }
    }
}
