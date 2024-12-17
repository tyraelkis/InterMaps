package uji.es.intermaps.Interfaces

import androidx.annotation.BoolRes
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes

interface ORSRepository {
    suspend fun searchInterestPlaceByCoordinates(coordinate: Coordinate):InterestPlace
    suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace
    suspend fun calculateRoute(origin: Coordinate, destination: Coordinate): Route
    suspend fun calculateFuelConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double
    suspend fun calculateElectricConsumition(route: Route, transportMethod: TransportMethods, vehicleType: VehicleTypes): Double
    //suspend fun calculateCaloriesConsumition(route: Route, transportMethod: TransportMethods): Double

}