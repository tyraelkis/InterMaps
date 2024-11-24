package uji.es.intermaps.Model

import uji.es.intermaps.Exceptions.IncorrectDataException

class UserService(var repository: Repository) {

    fun createUser(email: String, pswd:String): User {
        //if(email.isEmpty() || pswd.isEmpty()) throw IncorrectDataException("Los campos deben tener contenido")
        //val user = User(email, pswd)
        //return repository.createUser(user)
        return User(email, pswd)
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