package uji.es.intermaps.Model

data class Route(
    val origin: String,
    val destination: String,
    val route: List<String>,
    val distance: Double,
    val duration: Double,
    val cost: Double = 0.0,
    val routeType: RouteTypes = RouteTypes.RAPIDA,
    val fav: Boolean = false,
    val trasnportMethod: TrasnportMethods = TrasnportMethods.VEHICULO,
    val vehiclePlate: String = "")