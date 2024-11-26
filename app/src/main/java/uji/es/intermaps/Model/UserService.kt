package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import uji.es.intermaps.Exceptions.NotValidUserData


class UserService(var repository: Repository) {

    suspend fun createUser(email: String, pswd: String): User {
        // Validación local
        if (email.isBlank() || pswd.isBlank()) {
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos.")
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("El correo electrónico no tiene un formato válido.")
        }
        if (pswd.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.")
        }
        val pswdRegex = "^[A-Za-z0-9]+$".toRegex()
        if (!pswd.matches(pswdRegex)) {
            throw IllegalArgumentException("La contraseña no tiene un formato válido.")
        }

        // Delegar la creación al repositorio
        return repository.createUser(email, pswd)
    }

    suspend fun login(email: String, pswd: String) : Boolean{
        if (email.isBlank() || pswd.isBlank()) {
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos.")
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("El correo electrónico no tiene un formato válido.")
        }
        if (pswd.length < 6) {
            throw IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.")
        }
        val pswdRegex = "^[A-Za-z0-9]+$".toRegex()
        if (!pswd.matches(pswdRegex)) {
            throw IllegalArgumentException("La contraseña no tiene un formato válido.")
        }

        return repository.loginUser(email,pswd)
    }

    suspend fun signOut() : Boolean{
        return repository.signOut()
    }

    fun editUserData(email: String, newPassword:String): Boolean{
        return true
    }

    fun viewUserData(email: String): User?{
        val user: User
        if (email != null || email != ""){
            user = repository.viewUserData(email)!!
        }
        else{
            return null
        }
        return user
    }

    fun deleteUser(email: String){

    }

    val db = FirebaseFirestore.getInstance()
    fun conection_db () {
        db.collection("Test")
            .add(mapOf("key" to "value"))
            .addOnSuccessListener { Log.d("Firestore", "Conexión exitosa") }
            .addOnFailureListener {
                Log.e("Firestore", "Error al conectar", it)
            }
    }

}