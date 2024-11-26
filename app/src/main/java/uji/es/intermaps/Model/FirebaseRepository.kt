package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.Callback

class FirebaseRepository: Repository{
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth


    override fun createUser(email:String, pswd: String): User{
        return User("a","b")
    }

    override fun loginUser(email:String, pswd: String): Boolean{
        return false
    }

    override fun viewUserData(email: String): User?{
        return null
    }

    override fun editUserData(email: String, newPassword:String): Boolean{
        return true
    }

    override fun deleteUser(email: String): Boolean{
        return false
    }

    override fun setAlias(interestPlace: InterestPlace, newAlias : String, callback: (Boolean) -> Unit){
        val latitude = interestPlace.coordinate.latitude
        val longitude = interestPlace.coordinate.longitude
        val geoPoint = GeoPoint(latitude, longitude)
        db.collection("InterestPlace")
            .whereEqualTo("coordiante", geoPoint)
            .get()
            .addOnSuccessListener { documents ->
                if(documents.isEmpty()){
                    callback(false)
                }else{
                    val document = documents.documents[0]
                    val documentId = document.id

                    db.collection("InterestPlace")
                        .document(documentId)
                        .update("alias", newAlias)
                        .addOnSuccessListener {
                            callback(true)
                        }
                        .addOnFailureListener{ e ->
                            callback(false)
                        }
                }
            }
            .addOnFailureListener{e ->
                callback(false)
            }


    }

    override fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String, fav: Boolean) {

    }

    override fun deleteInterestPlace(coordinate: Coordinate): Boolean {
        return false
    }

}


