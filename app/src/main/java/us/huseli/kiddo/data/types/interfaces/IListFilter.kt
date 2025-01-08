package us.huseli.kiddo.data.types.interfaces

interface IListFilter<T : IListFilter<T, Rule>, Rule : IListFilterRule<*>> {
    val and: List<T>?
    val or: List<T>?
    val rule: Rule?

    fun getParams(): Map<String, Any> {
        val argCount = listOfNotNull(and, or, rule).size

        require(argCount == 1)
        return and?.let { mapOf("and" to it.map { it.getParams() }) }
            ?: or?.let { mapOf("or" to it.map { it.getParams() }) }
            ?: rule!!.getParams()
    }
}
