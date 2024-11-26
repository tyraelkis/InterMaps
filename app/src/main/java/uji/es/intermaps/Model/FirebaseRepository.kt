package uji.es.intermaps.Model

import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnregistredUserException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

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


