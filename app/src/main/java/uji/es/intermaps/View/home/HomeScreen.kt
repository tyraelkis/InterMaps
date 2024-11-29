package uji.es.intermaps.View.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uji.es.intermaps.Model.DataBase.auth
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.ViewModel.UserService
import uji.es.intermaps.R

@Composable
fun HomeScreen (navigateToUserDataScreen: () -> Unit = {}, navigateToInterestPlaceList: () -> Unit){

    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val userService = UserService(repository)
    //var email by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(150.dp))
        Text(
            text = "Pantalla principal",
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(150.dp))

        Text(
            text = "Has iniciado sesion",
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )

        Image(
            painter = painterResource(
                id = R.drawable.logogrande
            ),
            contentDescription = "",
            modifier = Modifier
                .width(320.dp) // Ancho específico
                .height(270.dp)
        )

        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = { navigateToUserDataScreen()
                coroutineScope.launch {
                    val emailExists = userService.viewUserData(user?.email.toString())
                    if (emailExists) {
                        navigateToUserDataScreen() // Navega si el correo existe
                    } else {
                        Log.d("Firestore", "El correo no está registrado.")
                    }
                }
                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Pantalla datos usuario", color = White, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(25.dp))


        Button(
            onClick = {
                navigateToInterestPlaceList()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Pantalla lugares de interés", color = White, fontWeight = FontWeight.Bold)
        }
    }
}