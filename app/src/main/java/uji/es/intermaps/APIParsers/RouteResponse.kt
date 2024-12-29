package uji.es.intermaps.APIParsers

import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("routes")
    val routes: List<RouteFeature>
)

data class RouteFeature(
    @SerializedName("geometry")
    val geometry: String,

    @SerializedName("summary")
    val summary: RouteSummary
)

data class RouteSummary(
    @SerializedName("distance")
    val distance: Double,
    @SerializedName("duration")
    val duration: Double
)