package uji.es.intermaps.Interfaces

import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TrasnportMethods
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.Vehicle

interface Repository {
    suspend fun createUser(email:String, pswd: String): User
    suspend fun loginUser(email:String, pswd: String): Boolean
    suspend fun signOut(): Boolean
    suspend fun viewUserData(email: String) : Boolean
    suspend fun editUserData(newPassword:String): Boolean
    suspend fun deleteUser(email: String, password: String): Boolean
    suspend fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean
    suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace
    suspend fun viewInterestPlaceData(coordinate: Coordinate): InterestPlace
    suspend fun viewInterestPlaceList(): List<InterestPlace>
    suspend fun deleteInterestPlace(coordinate: Coordinate): Boolean
    suspend fun getInterestPlaceByToponym(toponym: String): InterestPlace
    suspend fun createVehicle(plate: String,type: String, consumption: Double): Vehicle
    suspend fun deleteVehicle(plate: String): Boolean
    suspend fun viewVehicleList(): List<Vehicle>
    suspend fun createRoute(origin: String, destination: String, trasnportMethod: TrasnportMethods): Route
}