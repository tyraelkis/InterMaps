package uji.es.intermaps.Interfaces

import androidx.navigation.ActivityNavigator
import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.Model.VehicleTypes

interface Repository {
    suspend fun createUser(email: String, pswd: String): User
    suspend fun loginUser(email: String, pswd: String): Boolean
    suspend fun signOut(): Boolean
    suspend fun viewUserData(email: String): Boolean
    suspend fun editUserData(newPassword: String): Boolean
    suspend fun deleteUser(email: String, password: String): Boolean
    suspend fun setAlias(interestPlace: InterestPlace, newAlias: String): Boolean
    suspend fun createInterestPlace(
        coordinate: Coordinate,
        toponym: String,
        alias: String
    ): InterestPlace

    suspend fun viewInterestPlaceData(coordinate: Coordinate): InterestPlace
    suspend fun viewInterestPlaceList(): List<InterestPlace>
    suspend fun deleteInterestPlace(coordinate: Coordinate): Boolean
    suspend fun getInterestPlaceByToponym(toponym: String): InterestPlace
    suspend fun createVehicle(plate: String, type: String, consumption: Double): Vehicle
    suspend fun deleteVehicle(plate: String): Boolean
    suspend fun viewVehicleList(): List<Vehicle>
    suspend fun saveRouteToDatabase(route: Route)
    suspend fun getAverageFuelPrices(): List<Double>
    suspend fun getElectricPrice(): Double
    suspend fun viewVehicleData(plate: String): Vehicle
    suspend fun editVehicleData(plate: String, newType: String, newConsumption: Double): Boolean
    suspend fun getVehicleTypeAndConsump(route: Route): Pair<VehicleTypes, Double>
    suspend fun viewRouteList(): List<Route>
    suspend fun deleteRoute (route: Route): Boolean
}