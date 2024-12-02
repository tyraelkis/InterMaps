package uji.es.intermaps.ViewModel

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.User
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
                    }else {
                        val exception = task.exception
                        if (exception is FirebaseAuthUserCollisionException) {
                            continuation.resumeWithException(
                                AccountAlreadyRegistredException("Ya existe una cuenta con este email")
                            )
                        }
                        else if (exception is FirebaseAuthInvalidCredentialsException){
                            continuation.resumeWithException(
                                IllegalArgumentException("El correo o la contraseña no tienen un formato válido")
                            )
                        }else {
                            continuation.resumeWithException(exception ?: Exception("Error desconocido al crear el usuario."))
                        }
                    }
                }
        }
    }


    override suspend fun loginUser(email:String, pswd: String): Boolean{
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
                            continuation.resumeWithException(exception ?: Exception("Error desconocido al iniciar sesión."))
                        }
                    }
                }
        }
    }

    override suspend fun signOut() : Boolean{
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
                    Log.e("Firestore", "Error al verificar el correo: ${exception.message}", exception)
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
        return try{
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

    override suspend fun setAlias(interestPlace: InterestPlace, newAlias: String):Boolean {
        return try{
            val coordinate = interestPlace.coordinate

            val search = db.collection("InterestPlace")
                .whereEqualTo("coordinate", coordinate)
                .get()
                .await()
            if(search.isEmpty){
                false
            }else{
                val document = search.documents[0]
                val documentId = document.id

                db.collection("InterestPlace")
                    .document(documentId)
                    .update("alias", newAlias)
                    .await()
                true
            }
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
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
            .addOnFailureListener { _ ->
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
            .addOnFailureListener{ _ ->
                callback(false, emptyList())
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


    override suspend fun getInterestPlaceByToponym(
        toponym: String,
        callback: ((Boolean), (List<InterestPlace>)) -> Unit
    ) {
        return suspendCoroutine { continuation ->
            Log.d("toponym", toponym)
            db.collection("InterestPlace")
                .whereEqualTo("toponym", toponym)
                .get()
                .addOnSuccessListener { documents ->
                    Log.i("getInteresetPlaceByToponym", "${documents.documents}")
                    if (!documents.isEmpty) {
                        val favList = mutableListOf<InterestPlace>()
                        for (document in documents) {

                            val interestPlace = document.toObject(InterestPlace::class.java)
                            favList.add(interestPlace)
                        }
                        callback(true, favList)
                    } else {
                        callback(false, emptyList())
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("getInteresetPlaceByToponym", e.message ?: "")
                    callback(false, emptyList())
                }
        }
    }

    override suspend fun searchInterestPlace(coordinate: Coordinate) : InterestPlace {
        TODO("Not yet implemented")
    }

    override suspend fun viewInterestPlaceData(coordinate: Coordinate): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun searchInterestPlaceByToponym(toponym: String) : InterestPlace {
        TODO("Not yet implemented")
    }

    override fun viewInterestPlaceList(callback: (List<InterestPlace>) -> Unit) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteInterestPlace(coordinate: Coordinate): Boolean {
        return try {
            Log.d("deleteInterestPlace", "Starting deletion for coordinate: $coordinate")

            val querySnapshot = DataBase.db.collection("InterestPlace")
                .whereEqualTo("coordinate", coordinate)
                .get()
                .await()

            if (querySnapshot.isEmpty) {
                Log.d("deleteInterestPlace", "No documents found for coordinate: $coordinate")
                return false
            }

            querySnapshot.documents.forEach { document ->
                DataBase.db.collection("InterestPlace")
                    .document(document.id)
                    .delete()
                    .await() // Convierte a una operación suspendida
                Log.d("deleteInterestPlace", "Document with ID ${document.id} deleted successfully.")
            }
            true
        } catch (e: Exception) {
            Log.e("deleteInterestPlace", "Error while deleting documents: ${e.message}", e)
            false
        }
        return false
    }
}


