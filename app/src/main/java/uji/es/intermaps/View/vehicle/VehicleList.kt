package uji.es.intermaps.View.vehicle

import androidx.compose.foundation.Image
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.VehicleViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun VehicleList(auth: FirebaseAuth, navController: NavController, viewModel: VehicleViewModel) {
    val email = auth.currentUser?.email
    var allVehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }

    LaunchedEffect(email) {
        if (email != null) {
            allVehicles = viewModel.viewVehicleList()
        }
    }

    val favList by remember(allVehicles) { derivedStateOf { allVehicles.filter { it.fav } } }
    val noFavList by remember(allVehicles) { derivedStateOf { allVehicles.filter { !it.fav } } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.icono_cerrar
                ),
                contentDescription = "",
                modifier = Modifier
                    .size(25.dp)
                    .clickable { navController.navigate("mainMenu") },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Lista de vehículos de\n")
                    append(email?.substringBefore("@") ?: "Usuario")
                },
                color = Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(lineHeight = 35.sp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Tus vehículos favoritos",
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
                favList.forEach { vehicle ->

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
                                .size(30.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (viewModel.deleteFavVehicle(vehicle.plate)) {
                                            withContext(Dispatchers.Main) {
                                                allVehicles = allVehicles.map {
                                                    if (it == vehicle) viewModel.cloneWithFav(vehicle, false) else it
                                                }
                                            }
                                        }
                                    }
                                },
                            tint = Color(color = 0XFF007E70)
                        )
                        Text(
                            text = vehicle.plate,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    viewModel.updateVehicle(vehicle)
                                    val encodedPlate = URLEncoder.encode(vehicle.plate, StandardCharsets.UTF_8.toString())
                                    navController.navigate("vehicleEditDelete/$encodedPlate")
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
                text = "Vehículos",
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
                noFavList.forEach { notFavVehicle ->
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
                                .size(30.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (viewModel.setFavVehicle(notFavVehicle.plate)) {
                                            withContext(Dispatchers.Main) {
                                                allVehicles = allVehicles.map {
                                                    if (it == notFavVehicle) viewModel.cloneWithFav(notFavVehicle, true) else it
                                                }
                                            }
                                        }
                                    }
                                },
                            tint = Black
                        )

                        Text(
                            text = notFavVehicle.plate,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    viewModel.updateVehicle(notFavVehicle)
                                    val encodedPlate = URLEncoder.encode(notFavVehicle.plate, StandardCharsets.UTF_8.toString())
                                    navController.navigate("vehicleEditDelete/$encodedPlate")
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
                navController.navigate("vehicleCreate")
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
                text = "Añadir vehículo",
                fontSize = 20.sp,
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

    }
}
