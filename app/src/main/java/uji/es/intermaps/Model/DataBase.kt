package uji.es.intermaps.Model

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.internal.Logger.TAG
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import uji.es.intermaps.Exceptions.NotSuchPlaceException

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
            val userEmail = auth.currentUser?.email.toString()
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (!documentSnapshot.exists()) {
                return false
            }

            val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

            interestPlaces.any { place ->
                val placeCoordinate = place["coordinate"] as? Map<String, Double> ?: return@any false
                val latitude = placeCoordinate["latitude"] ?: return@any false
                val longitude = placeCoordinate["longitude"] ?: return@any false

                latitude == coordinate.latitude && longitude == coordinate.longitude
            }
        } catch (exception: Exception) {
            Log.e("doesInterestPlaceExists", "Error al verificar el lugar de interés: ${exception.message}", exception)
            false
        }
    }

    suspend fun doesVehicleExist(plate: String): Boolean{
        TODO()
    }
}