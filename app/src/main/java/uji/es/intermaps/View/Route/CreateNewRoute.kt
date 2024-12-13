package uji.es.intermaps.View.Route

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.TrasnportMethods
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.RouteService
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun CreateNewRoute(auth: FirebaseAuth, navController: NavController, viewModel: InterestPlaceViewModel) {
    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val interestPlaceService = InterestPlaceService(repository)
    val routeService = RouteService(repository)
    var allPlaces by remember { mutableStateOf<List<InterestPlace>>(emptyList()) }
    val emailPrefix = user?.email?.substringBefore("@") ?: "Usuario"
    var origin = ""
    var destination = ""


    LaunchedEffect(user?.email) {
        if (user?.email != null) {
            try {
                val places = interestPlaceService.viewInterestPlaceList()
                allPlaces = places
            } catch (e: Exception) {
                allPlaces = emptyList()
            }
        }
    }

    val favList = allPlaces.filter { it.fav }
    val noFavList = allPlaces.filter { !it.fav }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                Color.White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Lista de lugares de\n")
                    append("\n")
                    append(emailPrefix)
                },
                color = Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center // Centrar el texto dentro del Row
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Tus lugares favoritos",
                color = Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {

            if (favList.isNotEmpty()) {
                favList.forEach { place ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Estrella fav",
                            modifier = Modifier
                                .size(30.dp),
                            tint = Color(color = 0XFF007E70)
                        )
                        if(place.alias.isEmpty()) {
                            Text(
                                text = place.toponym,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp)
                            )
                        }
                        else {
                            Text(
                                text = place.alias,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    viewModel.updateInterestPlace(place)
                                    val encodedToponym = URLEncoder.encode(place.toponym, StandardCharsets.UTF_8.toString())
                                    Log.d("encodedToponym", encodedToponym)
                                    navController.navigate("interestPlaceSetAlias/$encodedToponym")
                                },

                            tint = Color(color = 0XFF007E70)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Lugares de interés",
                color = Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {
            if (noFavList.isNotEmpty()) {
                noFavList.forEach { notFavPlace ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Estrella fav",
                            modifier = Modifier
                                .size(30.dp),
                            tint = Black
                        )
                        if(notFavPlace.alias.isEmpty()) {
                            Text(
                                text = notFavPlace.toponym,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )
                        } else {
                            Text(
                                text = notFavPlace.alias,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                            )
                        }

                        Button(
                            onClick = {
                                origin = notFavPlace.toponym
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Black
                            )
                        ) {
                            Text(
                                text = "Origen",
                                fontSize = 16.sp,
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Button(
                            onClick = {
                                destination = notFavPlace.toponym
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Black
                            )
                        ) {
                            Text(
                                text = "Destino",
                                fontSize = 16.sp,
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    viewModel.updateInterestPlace(notFavPlace)
                                    val encodedToponym = URLEncoder.encode(notFavPlace.toponym, StandardCharsets.UTF_8.toString())
                                    Log.d("encodedToponym", encodedToponym)
                                    navController.navigate("interestPlaceSetAlias/$encodedToponym")
                                },
                            tint = Black
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    routeService.createRoute(origin, destination, TrasnportMethods.VEHICULO)
                }
            },
            modifier = Modifier
                .width(350.dp)
                .height(45.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Black
            )
        ) {
            Text(
                text = "Crear ruta",
                fontSize = 20.sp,
            )
        }

    }
}
