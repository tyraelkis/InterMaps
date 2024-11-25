package uji.es.intermaps.Model

import com.google.firebase.auth.FirebaseAuth
import uji.es.intermaps.Exceptions.NotValidUserData


class UserService(var repository: Repository) {

    fun createUser(email: String, pswd:String): User {
        if (email.isNullOrBlank()|| pswd.isNullOrBlank()){
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos o nulos.")
        }
        else{
            val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
            if (!email.matches(emailRegex)) {
                throw IllegalArgumentException("El correo electrónico no tiene un formato válido.")
            }
            if (pswd.length < 8) {
                throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.")
            }
        }
        return User(email, pswd)
        //Comprueba las reglas de negocio
    }

    fun login(email: String, pswd: String) : Boolean{
        return false
    }

    fun signOut(email: String, pswd: String) : Boolean{
        return false
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
}