package uji.es.intermaps.Interfaces

import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes

interface ORSRepository {
    suspend fun searchInterestPlaceByCoordinates(coordinate: Coordinate):InterestPlace
    suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace
    suspend fun calculateRoute(origin: String, destination: String, transportMethod: TransportMethods,routeType: RouteTypes) : RouteFeature
    suspend fun calculateConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double
    suspend fun calculateCaloriesConsumition(route: Route, transportMethod: TransportMethods): Double
    suspend fun createRoute( origin: String, destination: String,
                             transportMethods: TransportMethods,
                             routeType: RouteTypes, vehiclePlate: String, route: RouteFeature): Route
    suspend fun getRoute(origin: String, destination: String, transportMethod: TransportMethods,
                         vehiclePlate: String): Route?
}