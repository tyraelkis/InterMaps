package uji.es.intermaps.ViewModel

import com.mapbox.maps.extension.style.expressions.dsl.generated.featureState
import uji.es.intermaps.Exceptions.IncorrectDataException
import uji.es.intermaps.Exceptions.NotSuchTransportException
import uji.es.intermaps.Exceptions.NotSuchVehicleException
import uji.es.intermaps.Exceptions.NotValidParameterException
import uji.es.intermaps.Exceptions.NotValidUserData
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnableToDeleteUserException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.User

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

    suspend fun editUserData(newPassword:String): Boolean{
        if (newPassword.isBlank()) {
            throw NotValidUserData("El correo electrónico y la contraseña no pueden estar vacíos.")
        }
        if (newPassword.length < 8) {
            throw IncorrectDataException()
        }
        repository.editUserData(newPassword)
        return true
    }

    suspend fun viewUserData(email: String): Boolean{
        if (repository.viewUserData(email))
            return true
        throw SessionNotStartedException()

    }

    suspend fun deleteUser(email: String, password: String): Boolean{
       if (repository.deleteUser(email,password)) {
           return true
       }
        throw UnableToDeleteUserException()
    }

    suspend fun updateUserVehicle(vehiclePlate: String): Boolean{
        if (vehiclePlate.isBlank()) {
            throw NotSuchVehicleException()
        }
        repository.updateUserAttribute("preferredVehicle", vehiclePlate)
        return true
    }

    suspend fun updateUserTransportMethod(transportMethod: String): Boolean{
        if (transportMethod.isBlank()) {
            throw NotSuchTransportException()
        }
        repository.updateUserAttribute("preferredTransportMethod", transportMethod)
        return true
    }

    suspend fun getUserAttribute(attributeName: String): Any?{
        return repository.getUserAttribute(attributeName)
    }

    suspend fun updateUserRouteType(routeType: String): Boolean{
        if (routeType.equals("Ninguno")) {
            repository.updateUserAttribute("preferredRouteType", routeType)
            return true
        }
        if (!RouteTypes.entries.any(){it.name == routeType})
            throw NotValidParameterException()
        repository.updateUserAttribute("preferredRouteType", routeType)
        return true
    }


}