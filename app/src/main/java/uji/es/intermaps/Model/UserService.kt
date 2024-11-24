package uji.es.intermaps.Model

import com.google.firebase.auth.FirebaseAuth

class UserService(var repository: Repository) {

    fun createUser(email: String, pswd:String): User {
        //Comprueba las reglas de negocio
        return User("a","b");
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
        return null
    }

    fun deleteUser(email: String){

    }
}