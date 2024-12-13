package uji.es.intermaps.APIParsers

import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("features")
    val features: List<RouteFeature>
)

data class RouteFeature(
    @SerializedName("geometry")
    val geometry: RouteGeometry
)

data class RouteGeometry(
    @SerializedName("coordinates")
    val coordinates: List<List<Double>> // Lista de coordenadas del recorrido
)