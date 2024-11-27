package uji.es.intermaps.Model

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

    fun editUserData(newPassword:String): Boolean{
        if (newPassword.isBlank()) {
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos.")
        }
        if (newPassword.length < 8) {
            throw IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.")
        }
        repository.editUserData(newPassword)
        return true
    }

    suspend fun viewUserData(email: String): Boolean{
        return repository.viewUserData(email)

    }

    fun deleteUser(email: String, password: String): Boolean{
        return repository.deleteUser(email, password)

    }

}