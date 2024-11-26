package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import uji.es.intermaps.Exceptions.NotValidUserData


class UserService(var repository: Repository) {

    suspend fun createUser(email: String, pswd: String, vehicle: String): User {
        // Validación local
        if (email.isBlank() || pswd.isBlank()) {
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos.")
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!email.matches(emailRegex)) {
            throw IllegalArgumentException("El correo electrónico no tiene un formato válido.")
        }
        if (pswd.length < 8) {
            throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.")
        }
        return repository.createUser(email, pswd, vehicle)
    }

    fun login(email: String, pswd: String) : Boolean{
        return false
    }

    fun signOut(email: String, pswd: String) : Boolean{
        return false
    }

    /*suspend fun editUserEmail(newEmail: String) : Boolean{
        if (newEmail.isBlank()) {
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos.")
            return false
        }
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        if (!newEmail.matches(emailRegex)) {
            throw IllegalArgumentException("El correo electrónico no tiene un formato válido.")
            return false
        }
        repository.editUserEmail(newEmail)
        return true
    }*/

    fun editUserPassword(newPassword:String): Boolean{
        if (newPassword.isBlank()) {
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos.")
            return false
        }
        if (newPassword.length < 8) {
            throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.")
            return false
        }
        repository.editUserPassword(newPassword)
        return true
    }

    suspend fun viewUserData(email: String): User?{
        return repository.viewUserData(email)
    }

    fun deleteUser(email: String, password: String): Boolean{
        return repository.deleteUser(email, password)

    }

}