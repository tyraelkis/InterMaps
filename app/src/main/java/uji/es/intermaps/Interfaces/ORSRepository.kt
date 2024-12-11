package uji.es.intermaps.Interfaces

import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route

interface ORSRepository {
    suspend fun searchInterestPlaceByCoordinates(coordinate: Coordinate):InterestPlace
    suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace
    suspend fun calculateRoute(origin: Coordinate, destination: Coordinate): Route
    suspend fun getRegion(toponym: String): String

}