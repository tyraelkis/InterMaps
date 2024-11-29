package uji.es.intermaps.Model

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.internal.Logger.TAG
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

object DataBase {
    @SuppressLint("StaticFieldLeak")
    val db = Firebase.firestore
    val auth = Firebase.auth

    fun getNumberUsers(): Int {
        var count = 0
        val query = db.collection("Users")
        val countQuery = query.count()
        countQuery.get(AggregateSource.SERVER).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                count = task.result.count.toInt()
            } else {
                Log.d(TAG, "Count failed: ", task.exception)
            }
        }
        return count
    }

    suspend fun doesUserExist(email: String): Boolean {
        return try {
            val documents = db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .await()  //consulta asincrónica
            !documents.isEmpty
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun doesInteresPlaceExists(coordinate: Coordinate): Boolean {
        return try {
            val documents = db.collection("InterestPlace")
                .whereEqualTo("coordinate", coordinate)
                .get()
                .await()  //consulta asincrónica
            !documents.isEmpty
        } catch (exception: Exception) {
            false
        }
    }
}