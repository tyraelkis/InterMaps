package uji.es.intermaps.APIParsers

data class RouteResponse(
    val features: List<RouteFeature>
)

data class RouteFeature(
    val geometry: RouteGeometry
)

data class RouteGeometry(
    val coordinates: List<List<Double>> // Lista de coordenadas del recorrido
)