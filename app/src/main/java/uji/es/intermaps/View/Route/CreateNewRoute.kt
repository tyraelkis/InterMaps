package uji.es.intermaps.View.Route

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mapbox.maps.extension.style.expressions.dsl.generated.all
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TrasnportMethods
import uji.es.intermaps.View.CustomDropdownMenu
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
    var origin by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var expandedOrigin by remember { mutableStateOf(false) }
    var expandedDestination by remember { mutableStateOf(false) }
    var expandedTransport by remember { mutableStateOf(false) }
    var expandedRoutes by remember { mutableStateOf(false) }
    val toponyms = allPlaces.map { it.toponym }
    var routeType by remember { mutableStateOf(RouteTypes.RAPIDA) }
    var trasnportMethod by remember { mutableStateOf(TrasnportMethods.VEHICULO) }

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
                    text = (trasnportMethod.toString()),
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
                TrasnportMethods.entries.forEach { type ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = type.toString(),
                                modifier = Modifier
                                    .clickable {
                                        trasnportMethod = type
                                        expandedTransport = false
                                    }
                            )
                        },
                        onClick = {
                            trasnportMethod = type
                            expandedTransport = false
                        }
                    )

                }

            }
        }
        Spacer(modifier = Modifier.height(32.dp))
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


