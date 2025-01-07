package us.huseli.kiddo.data.requests

import us.huseli.kiddo.data.enums.GuiPropertyName
import us.huseli.kiddo.data.requests.interfaces.IRequestRefResult
import us.huseli.kiddo.data.types.GuiPropertyValue
import java.lang.reflect.Type

class GuiGetProperties(val properties: List<GuiPropertyName>) : IRequestRefResult<GuiPropertyValue> {
    override val typeOfResult: Type = GuiPropertyValue::class.java
    override val method: String = "GUI.GetProperties"

    override fun getParams(): Map<String, Any?> = mapOf("properties" to properties)
}
