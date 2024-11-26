package uji.es.intermaps.Model

import com.google.firebase.firestore.GeoPoint
import okhttp3.Callback

interface Repository {
    fun createUser(email:String, pswd: String): User
    fun loginUser(email:String, pswd: String): Boolean
    fun viewUserData(email: String) : User?
    fun editUserData(email: String, newPassword:String): Boolean
    fun deleteUser(email: String): Boolean
    fun setAlias(interestPlace: InterestPlace, newAlias : String, callback: (Boolean) -> Unit)
    fun createInterestPlace(coordinate: GeoPoint, toponym: String, alias: String, fav: Boolean)
    fun deleteInterestPlace(coordinate: GeoPoint): Boolean
    fun getFavList(callback: ((Boolean),(List<InterestPlace>)) -> Unit)
    fun getNoFavList(callback: ((Boolean),(List<InterestPlace>)) -> Unit)
}