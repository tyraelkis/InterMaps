package uji.es.intermaps.ViewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.NotValidUserData
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Model.TransportMethods


class UserViewModel(
    val userService: UserService,
    val auth: FirebaseAuth):
    ViewModel() {

    private val _showDeletePopUp = mutableStateOf(false)
    val showDialog: State<Boolean> get() = _showDeletePopUp

    private val _showUpdatePopUp = mutableStateOf(false)
    val showUpdateDialog: State<Boolean> get() = _showUpdatePopUp

    private val _showPasswordPopUp = mutableStateOf(false)
    val showPasswordDialog: State<Boolean> get() = _showPasswordPopUp

    private val _preferredTransport = mutableStateOf<TransportMethods?>(null)
    val preferredTransport: MutableState<TransportMethods?> get() = _preferredTransport

    private val _preferredVehicle = mutableStateOf<String?>(null)
    val preferredVehicle: MutableState<String?> get() = _preferredVehicle



    private var errorMessageNormal = ""

    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var newPassword by mutableStateOf("")
    var confirmPassword by mutableStateOf("")


    fun showDeletePopUp(){
        _showDeletePopUp.value = true
    }

    fun hideDeletePopUp(){
        _showDeletePopUp.value = false
    }

    fun showUpdatePopUp(){
        _showUpdatePopUp.value = true
    }

    fun hideUpdatePopUp(){
        _showUpdatePopUp.value = false
    }

    fun showPasswordPopUp(){
        _showPasswordPopUp.value = true
    }

    fun hidePasswordPopUp(){
        _showPasswordPopUp.value = false
    }




    val user = auth.currentUser


    fun deleteUser(navController: NavController){
        viewModelScope.launch {
            if (password.isNullOrBlank()) {
                errorMessage = "Introduce tu contraseña"
                return@launch
            }
            if (user != null) {
                val email = user.email.toString()
                val credential = EmailAuthProvider.getCredential(email, password)

                try {
                    user.reauthenticate(credential).await()

                    val result = userService.deleteUser(email, password)
                    if (result) {
                        hideDeletePopUp()
                        password = ""
                        navController.navigate("initial")
                    } else {
                        errorMessage = "No se pudo eliminar el usuario. Intenta de nuevo."
                        password = ""
                    }
                } catch (e: Exception) {
                    errorMessage = "Contraseña incorrecta. Inténtalo de nuevo."
                    password = ""
                }
            } else {
                Log.e("DeleteUser", "Usuario no autenticado")
                errorMessage = "No se encontró un usuario autenticado."
            }
        }
    }

    fun changeUserPassword() {
        viewModelScope.launch {
            try {
                if (newPassword == confirmPassword) {
                    userService.editUserData(newPassword)
                    hidePasswordPopUp()
                    showUpdatePopUp()
                    newPassword = ""
                    confirmPassword = ""
                } else {
                    errorMessage = "Las contraseñas no coinciden"
                }
            } catch (e: NotValidUserData) {
                errorMessage = e.message.toString()
            } catch (e: IllegalArgumentException) {
                errorMessage = e.message.toString()
            } catch (e: UnregistredUserException) {
                errorMessage = e.message.toString()
            } catch (e: Exception) {
                errorMessage = "La contraseña es muy corta o no cumple con los requisitos."
            }
        }

    }

    fun signOut (navController: NavController){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val success = userService.signOut()
                withContext(Dispatchers.Main) {
                    if (success) {
                        navController.navigate("initial")
                    } else {
                        errorMessage = "Error al cerrar sesión."
                        Log.e("SignOut", "No se pudo cerrar la sesión.")
                    }
                }
            } catch (e: SessionNotStartedException) {
                withContext(Dispatchers.Main) {
                    errorMessage = "No hay ninguna sesión iniciada."
                    Log.e("SignOut", e.message.toString())
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Ocurrió un error inesperado: ${e.message}"
                    Log.e("SignOut", e.message.toString(), e)
                }
            }
        }
    }


    suspend fun createUser(email: String, password: String){
        try {
            userService.createUser(email, password)
            errorMessageNormal = ""
        } catch (e: NotValidUserData) {
            errorMessageNormal = e.message.toString()
        } catch (e: IllegalArgumentException) {
            errorMessageNormal = e.message.toString()
        } catch (e: AccountAlreadyRegistredException) {
            errorMessageNormal = e.message.toString()
        }
    }

    suspend fun login(email: String, password: String){
        try {
            userService.login(email, password)
            errorMessageNormal = ""
        } catch (e: NotValidUserData) {
            errorMessageNormal = e.message.toString()
        } catch (e: IllegalArgumentException) {
            errorMessageNormal = e.message.toString()
        } catch (e: UnregistredUserException) {
            errorMessageNormal = e.message.toString()
        }
    }

    fun getErrorMessageNormal(): String{
        return errorMessageNormal
    }


}