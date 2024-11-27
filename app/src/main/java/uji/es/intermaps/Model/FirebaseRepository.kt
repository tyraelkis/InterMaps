package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnregistredUserException
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
                        val newUser = User(email)
                        //db.collection("Users").add(mapOf("email" to email))
                        db.collection("Users").add(mapOf("email" to email))
                            .addOnSuccessListener { documentReference ->
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
                        } else {
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
                        continuation.resume(true) //Funciona como el return de la coroutine
                    } else {
                        val exception = task.exception
                        if (exception is FirebaseAuthInvalidCredentialsException) {
                            continuation.resumeWithException(
                                UnregistredUserException("No existe un usuario con este correo electrónico")
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

    override suspend fun viewUserData(email: String): User?{
        return suspendCoroutine { continuation ->
            db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val user = User(
                            email = document.getString("email") ?: "")
                        continuation.resume(user)
                    } else {
                        continuation.resume(null)
                    }
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }
    }

    /*override fun editUserEmail(newEmail: String): Boolean {
        val user = auth.currentUser
        if (user == null) {
            Log.e("FirebaseAuth", "No hay un usuario autenticado")
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
        var result = false
        // Actualizar el correo electrónico en Firebase Authentication
        user.updateEmail(newEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FirebaseAuth", "Email cambiado exitosamente en Auth")

                    // Una vez actualizado en Auth, también lo actualizamos en Firestore
                    val db = FirebaseFirestore.getInstance()
                    val userRef = db.collection("users").document(user.uid)

                    // Actualizamos el correo electrónico en Firestore
                    userRef.update("email", newEmail)
                        .addOnCompleteListener { firestoreTask ->
                            if (firestoreTask.isSuccessful) {
                                Log.d("Firestore", "Email actualizado exitosamente en Firestore")
                                result = true
                            } else {
                                Log.e("Firestore", "Error al actualizar el email en Firestore", firestoreTask.exception)
                                result = false
                            }
                        }
                } else {
                    Log.e("FirebaseAuth", "Error al cambiar el email en Auth", task.exception)
                    result = false
                }
            }

        // Al estar realizando operaciones asíncronas, se debe esperar que el resultado se obtenga después de los callbacks.
        // Se recomienda manejar la respuesta después de completar ambos procesos (auth y firestore).

        return result
    }*/



    override fun editUserPassword(newPassword: String): Boolean {
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
            }

        return result
    }

    override fun deleteUser(email: String, password: String): Boolean {
        val user = auth.currentUser
        var res = false

        if (user == null) {
            Log.e("FirebaseAuth", "No hay un usuario autenticado")
            return res
        }
        // Eliminar los datos del usuario en Firestore
        db.collection("Users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val document = querySnapshot.documents[0]
                    db.collection("Users").document(document.id).delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Documento de usuario eliminado exitosamente.")
                            user.delete()
                                .addOnCompleteListener { deleteTask ->
                                    if (deleteTask.isSuccessful) {
                                        Log.d("FirebaseAuth", "Usuario eliminado exitosamente")
                                        res = true
                                    } else {
                                        Log.e("FirebaseAuth", "Error al eliminar usuario", deleteTask.exception)
                                    }
                                }
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Firestore", "Error al eliminar documento del usuario.", exception)
                        }
                } else {
                    Log.d("Firestore", "No se encontró el documento del usuario.")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error al buscar documentos del usuario.", exception)
            }

        return res
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


