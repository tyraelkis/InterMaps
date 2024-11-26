package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.Callback
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.collections.hashMapOf as hashMapOf

class FirebaseRepository: Repository{
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth


    override suspend fun createUser(email: String, pswd: String): User {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, pswd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newUser = User(email, pswd)
                        //db.collection("Users").add(mapOf("email" to email))
                        db.collection("Users").add("email" to email)
                            .addOnSuccessListener { documentReference ->
                                continuation.resume(newUser)
                            }
                            .addOnFailureListener { e ->
                                continuation.resumeWithException(e)
                            }
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Error desconocido al crear el usuario.")
                        )
                    }
                }
        }
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


