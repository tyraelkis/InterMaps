package uji.es.intermaps.Model

data class Route(
    val origin: String,
    val destination: String,
    val route: List<Coordinate>,
    val distance: Double,
    val duration: String,
    var cost: Double = 0.0,
    val routeType: RouteTypes = RouteTypes.RAPIDA,
    val fav: Boolean = false,
    val transportMethod: TransportMethods,
    val vehiclePlate: String = "")
