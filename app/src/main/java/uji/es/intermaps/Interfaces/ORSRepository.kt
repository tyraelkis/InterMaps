package uji.es.intermaps.Interfaces

import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace

interface ORSRepository {
    suspend fun searchInterestPlaceByCoordinates(coordinate: Coordinate):InterestPlace
    suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace
}