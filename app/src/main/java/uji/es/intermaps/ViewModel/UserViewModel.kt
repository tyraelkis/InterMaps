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
import kotlinx.coroutines.tasks.await


class UserViewModel(
    val userService: UserService,
    val auth: FirebaseAuth, navController: NavController):
    ViewModel() {

    var popup by mutableStateOf("")
            protected set
    var password by mutableStateOf("")
    var errorMessage by mutableStateOf("")
    var navigateToInitial by mutableStateOf(false)
        private set


    /*suspend fun deleteUser (email: String, password: String): Boolean{
        return try {
            val result = userService.deleteUser(email, password)
            result
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error al eliminar usuario: ${e.message}")
            false
        }
    }*/

    fun showPopUp(popup: String){
        this.popup = popup
    }

    fun hidePopUp(){
        this.popup = ""
    }

    val user = auth.currentUser


    suspend fun deleteUser(){
        viewModelScope.launch {
            if (user != null) {
                val email = user.email.toString()
                val credential = EmailAuthProvider.getCredential(email, password) // Crear credenciales

                try {
                    // Reautenticar al usuario
                    user.reauthenticate(credential).await()
                    Log.d("DeleteUser", "Reautenticación exitosa")

                    // Eliminar el usuario tras la reautenticación exitosa
                    val result = userService.deleteUser(email, password) // Llamada suspend
                    if (result) {
                        navigateToInitial = true
                        password = ""
                    } else {
                        errorMessage = "No se pudo eliminar el usuario. Intenta de nuevo."
                        password = ""
                    }
                } catch (e: Exception) {
                    Log.e("DeleteUser", "Error de reautenticación: ${e.message}")
                    errorMessage = "Contraseña incorrecta. Inténtalo de nuevo."
                    password = ""
                }
            } else {
                Log.e("DeleteUser", "Usuario no autenticado")
                errorMessage = "No se encontró un usuario autenticado."
            }
        }
    }

}