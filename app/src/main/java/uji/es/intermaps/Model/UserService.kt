package uji.es.intermaps.Model

class UserService(var repository: Repository) {

    fun createUser(email: String, pswd:String): User? {
        //Comprueba las reglas de negocio
        return null;
    }

    fun login(email: String, pswd: String) : Boolean{
        return false
    }

    fun signOut() : Boolean{
        return false
    }

    fun editUserData(email: String, newPassword:String): Boolean{
        return true
    }

    fun viewUserData(){

    }

    fun deleteUser(){

    }
}