package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
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
        val geoPoint = interestPlace.coordinate
        db.collection("InterestPlace")
            .whereEqualTo("coordinate", geoPoint)
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

    override fun createInterestPlace(coordinate: GeoPoint, toponym: String, alias: String, fav: Boolean) {

    }

    override fun deleteInterestPlace(coordinate: GeoPoint): Boolean {
        return false
    }

    override fun getFavList(callback:  ((Boolean),(List<InterestPlace>)) -> Unit){
        db.collection("InterestPlace")
            .whereEqualTo("fav", true)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty){
                    val favList = mutableListOf<InterestPlace>()
                    for (document in documents){
                        val interestPlace = document.toObject(InterestPlace::class.java)
                        favList.add(interestPlace)
                    }
                    callback(true, favList)
                }else{
                    callback(false,emptyList())
                }
            }
            .addOnFailureListener { e ->
                callback(false, emptyList())
            }
    }
    override fun getNoFavList(callback:  ((Boolean),(List<InterestPlace>)) -> Unit){
        db.collection("InterestPlace")
            .whereEqualTo("fav", false)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty){
                    val noFavList = mutableListOf<InterestPlace>()
                    for (document in documents){
                        val interestPlace = document.toObject(InterestPlace::class.java)
                        noFavList.add(interestPlace)
                    }
                    callback(true, noFavList)
                }else{
                    callback(false,emptyList())
                }
            }
            .addOnFailureListener{ e ->
                callback(false, emptyList())
            }
    }

}


