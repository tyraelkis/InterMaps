package uji.es.intermaps.Model

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.crashlytics.internal.Logger.TAG
import com.google.firebase.firestore.AggregateSource
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
            val userEmail = auth.currentUser?.email?: throw IllegalStateException("No hay un usuario autenticado")
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
        return try {
            val userEmail = auth.currentUser?.email?: throw IllegalStateException("No hay un usuario autenticado")
            val documentSnapshot = db.collection("Vehicle")
                .document(userEmail)
                .get()
                .await()

            if (!documentSnapshot.exists()) {
                return false
            }

            val vehicles = documentSnapshot.get("vehicles") as? List<Map<String, Any>> ?: emptyList()

            vehicles.any { vehicle ->
                val vehiclePlate = vehicle["plate"] ?: return@any false

                plate == vehiclePlate
            }
        } catch (exception: Exception) {
            Log.e("doesVehicleExists", "Error al verificar el vehiculo: ${exception.message}", exception)
            false
        }
    }

    suspend fun doesRouteExist(route: Route): Boolean{
        TODO()
    }
}