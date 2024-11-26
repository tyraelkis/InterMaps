package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseRepository: Repository{
    val db = FirebaseFirestore.getInstance()
    val auth = Firebase.auth


    override suspend fun createUser(email: String, pswd: String, vehicle: String): User {
        return suspendCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, pswd)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newUser = User(email, vehicle)
                        val userData = mutableMapOf<String, String> (
                            "email" to email,
                            "vehicle" to vehicle
                        )
                        db.collection("Users").add(userData)
                        //db.collection("Users").add(mapOf("vehicle" to vehicle))
                            .addOnSuccessListener { documentReference ->
                                continuation.resume(newUser) //Funciona como el return de la corutina
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

    override suspend fun viewUserData(email: String): User?{
        return suspendCoroutine { continuation ->
            db.collection("User")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val document = querySnapshot.documents[0]
                        val user = User(
                            email = document.getString("email") ?: "",
                            defaultVehicle = document.getString("vehicle") ?: ""
                        )
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

}


