package uji.es.intermaps.Interfaces

import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.User

interface Repository {
    suspend fun createUser(email:String, pswd: String): User
    suspend fun loginUser(email:String, pswd: String): Boolean
    suspend fun signOut(): Boolean
    suspend fun viewUserData(email: String) : Boolean
    suspend fun editUserData(newPassword:String): Boolean
    suspend fun deleteUser(email: String, password: String): Boolean
    suspend fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean
    suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace
    suspend fun viewInterestPlaceData(coordinate: Coordinate): Boolean
    suspend fun viewInterestPlaceList(email:String?): List<InterestPlace>
    fun deleteInterestPlace(coordinate: Coordinate): Boolean
    fun viewInterestPlaceList(callback: (List<InterestPlace>) -> Unit)
    suspend fun getInterestPlaceByToponym(toponym: String, callback: (Boolean, List<InterestPlace>) -> Unit)
}