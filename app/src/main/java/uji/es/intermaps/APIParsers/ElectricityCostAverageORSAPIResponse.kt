package uji.es.intermaps.APIParsers

import com.google.gson.annotations.SerializedName

data class ElectricityCostAverageORSAPIResponse(
    @SerializedName("included") val included: List<Included>
)

data class Included(
    @SerializedName("type") val type: String,
    @SerializedName("attributes") val attributes: Attributes
)

data class Attributes(
    @SerializedName("values") val values: List<Value>
)

data class Value(
    @SerializedName("value") val value: Double,
    @SerializedName("percentage") val percentage: Double,
    @SerializedName("datetime") val datetime: String
)


