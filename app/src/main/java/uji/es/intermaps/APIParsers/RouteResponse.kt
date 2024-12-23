package uji.es.intermaps.APIParsers

import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("features")
    val features: List<RouteFeature>
)

data class RouteFeature(
    @SerializedName("geometry")
    val geometry: RouteGeometry,

    @SerializedName("properties")
    val properties: RouteProperties
)


data class RouteGeometry(
    @SerializedName("coordinates")
    val coordinates: List<List<Double>>
)

data class RouteProperties(
    @SerializedName("summary")
    val summary: RouteSummary
)
data class RouteSummary(
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("duration")
    var duration: Double
)