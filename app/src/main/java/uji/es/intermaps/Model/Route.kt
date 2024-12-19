package uji.es.intermaps.Model

data class Route(
    val origin: String,
    val destination: String,
    val route: TransportMethods,
    val distance: Double,
    val duration: Double,
    var cost: Double = 0.0,
    val routeType: RouteTypes = RouteTypes.RAPIDA,
    val fav: Boolean = false,
    val trasnportMethod: TransportMethods = TransportMethods.VEHICULO,
    val vehiclePlate: String = "")
