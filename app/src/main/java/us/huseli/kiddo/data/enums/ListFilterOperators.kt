package us.huseli.kiddo.data.enums

import com.google.gson.annotations.SerializedName

@Suppress("unused")
enum class ListFilterOperators {
    @SerializedName("after") After,
    @SerializedName("before") Before,
    @SerializedName("between") Between,
    @SerializedName("contains") Contains,
    @SerializedName("doesnotcontain") DoesNotContain,
    @SerializedName("endswith") EndsWith,
    @SerializedName("false") False,
    @SerializedName("greaterthan") GreaterThan,
    @SerializedName("inthelast") InTheLast,
    @SerializedName("is") Is,
    @SerializedName("isnot") IsNot,
    @SerializedName("lessthan") LessThan,
    @SerializedName("notinthelast") NotInTheLast,
    @SerializedName("startswith") StartsWith,
    @SerializedName("true") True,
}
