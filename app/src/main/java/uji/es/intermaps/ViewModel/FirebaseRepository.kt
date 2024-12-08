package uji.es.intermaps.ViewModel

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import okhttp3.internal.cookieToString
import okhttp3.internal.toImmutableMap
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.Vehicle
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository: Repository {
    val db = DataBase.db
    val auth = DataBase.auth


    override suspend fun createUser(email: String, pswd: String): User {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, pswd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newUser = User(email, pswd)
                        db.collection("Users").document(email).set(mapOf("email" to email))
                            .addOnSuccessListener { _ ->
                                continuation.resume(newUser) //Funciona como el return de la coroutine
                            }
                            .addOnFailureListener { e ->
                                continuation.resumeWithException(e)
                            }
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            continuation.resumeWithException(
                                AccountAlreadyRegistredException("Ya existe una cuenta con este email")
                            )
                        } else if (exception is FirebaseAuthInvalidCredentialsException) {
                            continuation.resumeWithException(
                                IllegalArgumentException("El correo o la contraseña no tienen un formato válido")
                            )
                        } else {
                            continuation.resumeWithException(
                                exception ?: Exception("Error desconocido al crear el usuario.")
                            )
                        }
                    }
                }
        }
    }


    override suspend fun loginUser(email: String, pswd: String): Boolean {
        return suspendCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, pswd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(true)
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidCredentialsException) {
                            continuation.resumeWithException(
                                UnregistredUserException("Usuario o contraseña incorrectos")
                            )
                        } else {
                            continuation.resumeWithException(
                                exception ?: Exception("Error desconocido al iniciar sesión.")
                            )
                        }
                    }
                }
        }
    }

    override suspend fun signOut(): Boolean {
        if (auth.currentUser != null) {
            auth.signOut()
            return true
        } else {
            throw SessionNotStartedException("No hay ninguna sesión iniciada")
        }
    }

    override suspend fun viewUserData(email: String): Boolean {
        return suspendCoroutine { continuation ->
            db.collection("Users")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        continuation.resume(true) // El correo existe
                    } else {
                        continuation.resume(false) // El correo no existe
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(
                        "Firestore",
                        "Error al verificar el correo: ${exception.message}",
                        exception
                    )
                    continuation.resumeWithException(exception)
                }
        }
    }

    override suspend fun editUserData(newPassword: String): Boolean {
        val user = auth.currentUser
        if (user == null) {
            Log.e("FirebaseAuth", "No hay un usuario autenticado")
            return false
        }
        var result = false
        // Actualizar la contraseña
        user.updatePassword(newPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Contraseña cambiada exitosamente")
                    result = true
                } else {
                    Log.e("FirebaseAuth", "Error al cambiar la contraseña", task.exception)
                    result = false
                }
            }.await()

        return result
    }

    override suspend fun deleteUser(email: String, password: String): Boolean {
        return try {
            val user = auth.currentUser
            if (user == null) {
                Log.e("FirebaseAuth", "No hay un usuario autenticado")
            }
            // Buscar el documento del usuario en Firestore
            val querySnapshot = FirebaseFirestore.getInstance()
                .collection("Users")
                .whereEqualTo("email", email)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Log.d("Firestore", "No se encontró el documento del usuario.")
            }

            // Eliminar el documento de Firestore
            val documentId = querySnapshot.documents[0].id
            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(documentId)
                .delete()
                .await()
            Log.d("Firestore", "Documento de usuario eliminado exitosamente.")

            // Eliminar el usuario de FirebaseAuth
            user!!.delete().await()
            Log.d("FirebaseAuth", "Usuario eliminado exitosamente.")

            true
        } catch (e: Exception) {
            Log.e("DeleteUser", "Error al eliminar el usuario: ${e.message}", e)
            false
        }
    }

    override suspend fun setAlias(interestPlace: InterestPlace, newAlias: String): Boolean {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val interestPlaces =
                    documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

                val foundPlace = (interestPlaces.find { place ->
                    val coordinate = place["coordinate"] as? Map<String, Double>
                    val latitude = coordinate?.get("latitude") ?: 0.0
                    val longitude = coordinate?.get("longitude") ?: 0.0


                    latitude == interestPlace.coordinate.latitude && longitude == interestPlace.coordinate.longitude
                }?: throw NotSuchPlaceException("Lugar de interés no encontrado")).toMutableMap()

                val updatedInterestPlaces = interestPlaces.map { place ->
                    if (place == foundPlace) {
                        place.toMutableMap().apply {
                            this["alias"] = newAlias
                        }
                    }
                }
                db.collection("InterestPlace")
                    .document(userEmail)
                    .update("interestPlaces", updatedInterestPlaces)
                    .await()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("setAlias", "Error updating alias: ${e.message}", e)
            false
        }
    }


    override suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace {
        //Codigo original y funcional para crear lugar de interés
        /*return suspendCoroutine { continuation ->
            db.collection("InterestPlace").add(
                mapOf(
                    "coordinate" to coordinate,
                    "toponym" to toponym,
                    "alias" to alias,
                    "fav" to false
                )
            ).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(InterestPlace(coordinate, toponym, alias, false))
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Error desconocido al almacenar el lugar de interés."))
                }
            }
        }*/

        //Código modificado para poder relacionar un usuario con sus lugares
        //Siempre ocurre que el usuario no está autenticado pero la primera vez si que había funcionado así que no entiendo
        val email = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")
        return suspendCoroutine { continuation ->
            val newPlace = mapOf(
                "coordinate" to mapOf(
                    "latitude" to coordinate.latitude,
                    "longitude" to coordinate.longitude
                ),
                "toponym" to toponym,
                "alias" to alias,
                "fav" to false
            )

            val userDocument = db.collection("InterestPlace").document(email)
            //Intenta añadir a interestPlaces el nuevo lugar evitando duplicados con el FieldValue.arrayUnion
            userDocument.update("interestPlaces", FieldValue.arrayUnion(newPlace))
                .addOnSuccessListener {
                    continuation.resume(InterestPlace(coordinate, toponym, alias, false))
                }
                .addOnFailureListener { _ ->
                    // Si falla porque el usuario aún no tiene lugares guardados crea el documento con el atributo interestPlaces y le añade el lugar
                    userDocument.set(mapOf("interestPlaces" to listOf(newPlace)))
                        .addOnSuccessListener {
                            continuation.resume(InterestPlace(coordinate, toponym, alias, false))
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                }
        }
    }


    override suspend fun getInterestPlaceByToponym(toponym: String): InterestPlace{
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()
                val foundPlace = interestPlaces.find { place ->
                    val actualToponym = place["toponym"] as? String ?: ""
                    toponym.equals(actualToponym)
                } ?: throw NotSuchPlaceException("Lugar de interés no encontrado")

                var lat = (foundPlace["coordinate"] as Map<String, Double>)["latitude"] ?: 0.0
                var long = (foundPlace["coordinate"] as Map<String, Double>)["longitude"] ?: 0.0
                return InterestPlace(
                    coordinate =  Coordinate(lat,long),
                    toponym = foundPlace["toponym"] as? String ?: "",
                    alias = foundPlace["alias"] as? String ?: "",
                    fav = foundPlace["fav"] as? Boolean ?: false
                )
            } else {
                throw Exception("No existe el documento para el usuario: $userEmail")
            }
        } catch (e: Exception) {
            Log.e("GeneralError", "Ocurrió un error", e)
            throw e
        }
    }

    override suspend fun viewInterestPlaceData(interestPlaceCoordinate: Coordinate): InterestPlace {
        val userEmail = auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

                val foundPlace = interestPlaces.find { place ->
                    val coordinate = place["coordinate"] as? Map<String, Double>
                    val latitude = coordinate?.get("latitude") ?: 0.0
                    val longitude = coordinate?.get("longitude") ?: 0.0

                    latitude == interestPlaceCoordinate.latitude && longitude == interestPlaceCoordinate.longitude
                } ?: throw NotSuchPlaceException("Lugar de interés no encontrado")

                // Construir el objeto InterestPlace a partir del mapa encontrado
                return InterestPlace(
                    coordinate = interestPlaceCoordinate,
                    toponym = foundPlace["toponym"] as? String ?: "",
                    alias = foundPlace["alias"] as? String ?: "",
                    fav = foundPlace["fav"] as? Boolean ?: false
                )
            } else {
                throw Exception("No existe el documento para el usuario: $userEmail")
            }
        } catch (e: Exception) {
            Log.e("GeneralError", "Ocurrió un error", e)
            throw e
        }
    }


    override suspend fun viewInterestPlaceList(): List<InterestPlace> {
        val userEmail =  auth.currentUser?.email ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                // Extraemos el array interestPlaces del documento
                val interestPlaces = documentSnapshot.get("interestPlaces") as? List<Map<String, Any>> ?: emptyList()

                // Convertimos cada elemento del array a InterestPlace
                interestPlaces.map { place ->
                    InterestPlace(
                        coordinate = Coordinate(
                            latitude = (place["coordinate"] as Map<String, Double>)["latitude"] ?: 0.0,
                            longitude = (place["coordinate"] as Map<String, Double>)["longitude"] ?: 0.0
                        ),
                        toponym = place["toponym"] as String? ?: "",
                        alias = place["alias"] as String? ?: "",
                        fav = place["fav"] as Boolean? ?: false
                    )
                }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()  // En caso de error, retornamos una lista vacía
        }
    }

    override suspend fun deleteInterestPlace(coordinate: Coordinate): Boolean {
        val userEmail = auth.currentUser?.email
            ?: throw IllegalStateException("No hay un usuario autenticado")

        return try {
            val documentSnapshot = db.collection("InterestPlace")
                .document(userEmail)
                .get()
                .await()

            if (documentSnapshot.exists()) {
                val interestPlaces = documentSnapshot.get("interestPlaces") as? MutableList<Map<String, Any>>
                    ?: mutableListOf()

                val placeIndex = interestPlaces.indexOfFirst { place ->
                    val placeCoordinate = place["coordinate"] as? Map<String, Double> ?: return@indexOfFirst false
                    val latitude = placeCoordinate["latitude"] ?: return@indexOfFirst false
                    val longitude = placeCoordinate["longitude"] ?: return@indexOfFirst false

                    latitude == coordinate.latitude && longitude == coordinate.longitude
                }

                if (placeIndex == -1) {
                    throw NotSuchPlaceException("Lugar de interés no encontrado")
                }
                interestPlaces.removeAt(placeIndex)

                db.collection("InterestPlace")
                    .document(userEmail)
                    .update("interestPlaces", interestPlaces)
                    .await()

                true
            } else {
                false
            }
        } catch (e: NotSuchPlaceException) {
            Log.e("deleteInterestPlace", "Lugar no encontrado: ${e.message}")
            false
        } catch (e: Exception) {
            Log.e("deleteInterestPlace", "Error eliminando lugar: ${e.message}", e)
            false
        }
    }

    override suspend fun createVehicle(plate: String, type: String, consumption: Double):Vehicle {
        TODO("Not yet implemented")
    }

    override suspend fun deleteVehicle(plate: String): Boolean {
        TODO("Not yet implemented")
    }
}


