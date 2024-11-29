package uji.es.intermaps.Interfaces

import com.google.firebase.firestore.GeoPoint
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
    suspend fun createInterestPlace(coordinate: GeoPoint, toponym: String, alias: String): InterestPlace
    suspend fun searchInterestPlace(coordinate: GeoPoint) : InterestPlace
    suspend fun searchInterestPlaceByToponym(toponym: String) : InterestPlace
    suspend fun viewInterestPlaceData(coordinate: GeoPoint): Boolean
    fun deleteInterestPlace(coordinate: GeoPoint): Boolean
    fun getFavList(callback: ((Boolean),(List<InterestPlace>)) -> Unit)
    fun getNoFavList(callback: ((Boolean),(List<InterestPlace>)) -> Unit)
}