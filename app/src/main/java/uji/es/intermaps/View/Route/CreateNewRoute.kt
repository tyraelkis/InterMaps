package uji.es.intermaps.View.Route

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.TransportMethods.*
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.ViewModel.RouteViewModel
import uji.es.intermaps.ViewModel.UserViewModel
import uji.es.intermaps.ViewModel.VehicleService
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateNewRoute(auth: FirebaseAuth, navController: NavController, viewModel: RouteViewModel, userViewModel: UserViewModel) {
    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val interestPlaceService = InterestPlaceService(repository)
    val vehicleService = VehicleService(repository)
    val loading = viewModel.loading
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
    var routeType by remember { mutableStateOf(userViewModel.preferredRouteType.value ?: null) }
    var transportMethod by remember { mutableStateOf(userViewModel.preferredTransport.value ?: null) }
    var vehicle by remember {
        mutableStateOf(
            userViewModel.preferredVehicle.value ?: ""
        )
    }


    val isButtonEnabled by remember {
        derivedStateOf {
            if (transportMethod == VEHICULO) {
                origin.isNotEmpty() && destination.isNotEmpty() && vehicle.toString().isNotEmpty() && routeType != null && transportMethod != null
            } else {
                origin.isNotEmpty() && destination.isNotEmpty() && routeType != null && transportMethod != null
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
            vehicle = userViewModel.getPreferredVehicle()
            routeType = userViewModel.getPreferredRouteType()
                .takeUnless { it == "Ninguno" }
                ?.let { RouteTypes.valueOf(it) }

            transportMethod = userViewModel.getPreferredTransport()
                .takeUnless { it == "Ninguno" }
                ?.let { TransportMethods.valueOf(it) }
        }

    }
    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) { CircularProgressIndicator() }
    }else {

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
            Row(
                modifier = Modifier
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
                        .background(White)
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
                    modifier = Modifier.background(White)
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
            Row(
                modifier = Modifier
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
                        .background(White)
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
                    modifier = Modifier.background(White)
                ) {
                    toponyms.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    modifier = Modifier
                                        .clickable {
                                            destination = option
                                            expandedDestination = false
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
            Row(
                modifier = Modifier
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
                        .background(White)
                        .border(width = 1.dp, color = Color.Gray)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { expandedRoutes = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (routeType == null) {
                            "Selecciona una opción"
                        } else {
                            routeType.toString()
                        },
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
                    modifier = Modifier.background(White)
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
            Row(
                modifier = Modifier
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
                        .background(White)
                        .border(width = 1.dp, color = Color.Gray)
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { expandedTransport = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (transportMethod == null) {
                            "Selecciona una opción"
                        } else {
                            transportMethod.toString()
                        },
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
                    modifier = Modifier.background(White)
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
            if (transportMethod == VEHICULO) {
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
                            .background(White)
                            .border(width = 1.dp, color = Color.Gray)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { expandedVehicles = true }
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (vehicle == null) {
                                "Selecciona una opción"
                            } else {
                                vehicle.toString()
                            },
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
                        modifier = Modifier.background(White)
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
                    viewModel.updateRoute(
                        origin,
                        destination,
                        transportMethod,
                        routeType!!,
                        vehicle.toString()
                    )
                    viewModel.routeInDataBase = false
                    navController.navigate(
                        "viewRoute/" +
                                URLEncoder.encode(origin, StandardCharsets.UTF_8.toString()) + "/" +
                                URLEncoder.encode(
                                    destination,
                                    StandardCharsets.UTF_8.toString()
                                ) + "/" +
                                URLEncoder.encode(
                                    transportMethod.toString(),
                                    StandardCharsets.UTF_8.toString()
                                ) + "/" +
                                URLEncoder.encode(vehicle ?: "", StandardCharsets.UTF_8.toString())
                    )

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
}


