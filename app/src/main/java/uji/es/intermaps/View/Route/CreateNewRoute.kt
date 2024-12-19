package uji.es.intermaps.View.Route

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
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
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.RouteService
import uji.es.intermaps.ViewModel.RouteViewModel
import uji.es.intermaps.ViewModel.VehicleService

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateNewRoute(auth: FirebaseAuth, navController: NavController, viewModel: RouteViewModel) {
    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val interestPlaceService = InterestPlaceService(repository)
    val vehicleService = VehicleService(repository)
    val routeService = RouteService(repository)
    var allPlaces by remember { mutableStateOf<List<InterestPlace>>(emptyList()) }
    var allVehicles by remember { mutableStateOf<List<Vehicle>>(emptyList()) }
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var expandedOrigin by remember { mutableStateOf(false) }
    var expandedDestination by remember { mutableStateOf(false) }
    var expandedTransport by remember { mutableStateOf(false) }
    var expandedRoutes by remember { mutableStateOf(false) }
    var expandedVehicles by remember { mutableStateOf(false) }
    val toponyms = allPlaces.map { it.toponym }
    val plates = allVehicles.map { it.plate }
    var routeType by remember { mutableStateOf(RouteTypes.RAPIDA) }
    var transportMethod by remember { mutableStateOf(TransportMethods.VEHICULO) }
    var vehicle by remember { mutableStateOf("") }

    val isButtonEnabled by remember{
        derivedStateOf {
            if(transportMethod == TransportMethods.VEHICULO){
                origin.isNotEmpty() && destination.isNotEmpty() && vehicle.isNotEmpty()
            }else{
                origin.isNotEmpty() && destination.isNotEmpty()
            }
        }
    }



    LaunchedEffect(user?.email) {
        if (user?.email != null) {
            try {
                val places = interestPlaceService.viewInterestPlaceList()
                allPlaces = places
                val vehicles = vehicleService.viewVehicleList()
                allVehicles = vehicles
                } catch (e: Exception) {
                allPlaces = emptyList()
                allVehicles = emptyList()
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = ("Crear nuevas rutas"),
            color = Black,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,


        )

        //Origen
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ("Origen de la ruta"),
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(Color.White)
                    .border(width = 1.dp, color = Color.Gray)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { expandedOrigin = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (origin == "") {
                        "Selecciona una opción"
                    } else origin,
                    color = Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null, tint = Black,
                    modifier = Modifier.rotate(if (expandedOrigin) 180f else 0f)
                )
            }

            DropdownMenu(
                expanded = expandedOrigin,
                onDismissRequest = { expandedOrigin = false },
                modifier = Modifier.background(Color.White)
            ) {
                toponyms.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                modifier = Modifier
                                    .clickable {
                                        origin = option
                                        expandedOrigin = false
                                    }
                            )
                        },
                        onClick = {
                            origin = option
                            expandedOrigin = false
                        }

                    )
                }
            }
        }

        //Destino
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ("Destino de la ruta"),
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(Color.White)
                    .border(width = 1.dp, color = Color.Gray)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { expandedDestination = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (destination == "") {
                        "Selecciona una opción"
                    } else destination,
                    color = Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null, tint = Black,
                    modifier = Modifier.rotate(if (expandedDestination) 180f else 0f)
                )
            }

            // Menú desplegable
            DropdownMenu(
                expanded = expandedDestination,
                onDismissRequest = { expandedDestination = false },
                modifier = Modifier.background(Color.White)
            ) {
                toponyms.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                modifier = Modifier
                                    .clickable {
                                        destination = option
                                        expandedDestination= false
                                    }
                            )
                        },
                        onClick = {
                            destination = option
                            expandedDestination = false
                        }

                    )
                }
            }
        }

        //Tipo de ruta
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ("Tipo de ruta"),
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(Color.White)
                    .border(width = 1.dp, color = Color.Gray)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { expandedRoutes = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (routeType.toString()),
                    color = Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null, tint = Black,
                    modifier = Modifier.rotate(if (expandedRoutes) 180f else 0f)
                )
            }

            // Menú desplegable
            DropdownMenu(
                expanded = expandedRoutes,
                onDismissRequest = { expandedRoutes = false },
                modifier = Modifier.background(Color.White)
            ) {
               RouteTypes.entries.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = type.toString(),
                                modifier = Modifier
                                    .clickable {
                                        routeType = type
                                        expandedRoutes = false
                                    }
                            )
                        },
                        onClick = {
                            routeType = type
                            expandedRoutes = false
                        }
                    )

               }

            }
        }
        //Tipo de transporte
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = ("Tipo de metodo de transporte"),
                color = Black,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(32.dp)
                    .background(Color.White)
                    .border(width = 1.dp, color = Color.Gray)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { expandedTransport = true }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = (transportMethod.toString()),
                    color = Black,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Filled.ArrowDropDown,
                    contentDescription = null, tint = Black,
                    modifier = Modifier.rotate(if (expandedTransport) 180f else 0f)
                )
            }

            // Menú desplegable
            DropdownMenu(
                expanded = expandedTransport,
                onDismissRequest = { expandedTransport = false },
                modifier = Modifier.background(Color.White)
            ) {
                TransportMethods.entries.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = type.toString(),
                                modifier = Modifier
                                    .clickable {
                                        transportMethod = type
                                        expandedTransport = false
                                    }
                            )
                        },
                        onClick = {
                            transportMethod = type
                            expandedTransport = false
                        }
                    )

                }

            }
        }
        //Vehiculo elegido
        if(transportMethod == TransportMethods.VEHICULO) {
            Spacer(modifier = Modifier.height(18.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = ("Vehiculo"),
                    color = Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(32.dp)
                        .background(Color.White)
                        .border(width = 1.dp, color = Color.Gray)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { expandedVehicles = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (vehicle == "") {
                            "Selecciona una opción"
                        } else vehicle,
                        color = Black,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null, tint = Black,
                        modifier = Modifier.rotate(if (expandedVehicles) 180f else 0f)
                    )
                }

                // Menú desplegable
                DropdownMenu(
                    expanded = expandedVehicles,
                    onDismissRequest = { expandedVehicles = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    plates.forEach { plate ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = plate,
                                    modifier = Modifier
                                        .clickable {
                                            vehicle = plate
                                            expandedVehicles = false
                                        }
                                )
                            },
                            onClick = {
                                vehicle = plate
                                expandedVehicles = false
                            }
                        )

                    }

                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                viewModel.updateRoute(origin, destination, TransportMethods.VEHICULO, routeType, vehicle)
                navController.navigate("viewRoute")

            },
            enabled = isButtonEnabled,
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


