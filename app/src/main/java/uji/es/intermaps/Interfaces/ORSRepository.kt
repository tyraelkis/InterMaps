package uji.es.intermaps.Interfaces

import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TrasnportMethods

interface ORSRepository {
    suspend fun searchInterestPlaceByCoordinates(coordinate: Coordinate):InterestPlace
    suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace
    suspend fun calculateRoute(origin: String, destination: String, trasnportMethod: TrasnportMethods): RouteFeature
}