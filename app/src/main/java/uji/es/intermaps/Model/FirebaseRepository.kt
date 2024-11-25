package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.Callback
import kotlin.collections.hashMapOf as hashMapOf

class FirebaseRepository: Repository{
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth


    override fun createUser(email:String, pswd: String): User{
        auth.createUserWithEmailAndPassword(email, pswd)
        db.collection("User").add(email)
        return User(email,pswd)
    }

    override fun loginUser(email:String, pswd: String): Boolean{
        return false
    }

    override fun viewUserData(email: String): User?{
        val firebaseUser: FirebaseUser? = auth.currentUser

        return null
    }

    override fun editUserData(email: String, newPassword:String): Boolean{
        return true
    }

    override fun deleteUser(email: String): Boolean{
        return false
    }

    override fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean{
        return true
    }

    override fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace {
        return InterestPlace(Coordinate(0.0,0.0), "", "", false)
    }

    override fun deleteInterestPlace(coordinate: Coordinate): Boolean {
        return false
    }

}


