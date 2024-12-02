package uji.es.intermaps.View.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import kotlinx.coroutines.launch
import uji.es.intermaps.Model.DataBase.auth
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.ViewModel.UserService
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.UserViewModel

@Composable
fun HomeScreen (navController: NavController, viewModel: UserViewModel){

    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val userService = UserService(repository)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Mapa en la mitad superior
        MapboxMap(
            Modifier
                .fillMaxWidth()
                .weight(1f), // Ocupa la mitad del espacio
            mapViewportState = rememberMapViewportState {
                setCameraOptions {
                    zoom(11.0)
                    center(
                        Point.fromLngLat(
                            -0.0675,
                            39.9947
                        )
                    ) // Coordenadas para centrar el mapa en la UJI, Castellón de la Plana
                    pitch(0.0)
                    bearing(0.0)
                }
            }
        )

        // Texto en la mitad inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.8f), // Ocupa la otra mitad del espacio
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val coroutineScope = rememberCoroutineScope()

            Button(
                onClick = {
                    // Comentar esto para que solo puedas entrar si esta loggeado
                    navController.navigate("userDataScreen")
                    coroutineScope.launch {
                        val emailExists = userService.viewUserData(user?.email.toString())
                        if (emailExists) {
                            navController.navigate("userDataScreen")
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
                Text(
                    text = "Pantalla datos usuario",
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(25.dp))

            Button(
                onClick = {
                    navController.navigate("interestPlaceList")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Pantalla lugares de interés",
                    color = White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }


}