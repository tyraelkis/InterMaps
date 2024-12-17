package uji.es.intermaps.View.Route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.LiveData
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.ViewModel.RouteViewModel
import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ViewRoute(auth: FirebaseAuth, navController: NavController, viewModel: RouteViewModel) {

    val route = viewModel.route.value
    val origin = viewModel.route.value?.origin
    val destination = viewModel.route.value?.destination
    val transportMethod = viewModel.route.value?.trasnportMethod
    val routeType = viewModel.route.value?.routeType
    val points = viewModel.route.value?.route
    val distance = viewModel.route.value?.distance
    val duration = viewModel.route.value?.duration
    val cost = viewModel.route.value?.cost
    val vehiclePlate = viewModel.route.value?.vehiclePlate
    val loading = viewModel.loading



    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) { CircularProgressIndicator() }
    }else {
        Scaffold() { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                //Mapa con la ruta
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Mapa Simulado",
                        fontSize = 16.sp,
                        color = Color.DarkGray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Información de la ruta
                Text(
                    text = "${origin} -->${destination}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    LocationCard("${origin}", "Imagen 1")
                    LocationCard("${destination}", "Imagen 2")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Datos adicionales
                Text(
                    text = "Datos adicionales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Column(modifier = Modifier.padding(16.dp)) {
                    RouteDetailItem(label = "Método de transporte", value = "${transportMethod}")
                    if (transportMethod == TransportMethods.VEHICULO) {
                        RouteDetailItem(label = "Vehículo", value = "${vehiclePlate}")
                    }
                    RouteDetailItem(label = "Tipo de ruta", value = "${routeType}")
                    RouteDetailItem(label = "Distancia", value = "${distance} km")
                    RouteDetailItem(label = "Duración", value = "${duration} min")
                    //RouteDetailItem(label = "Coste", value = "2€")
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botón Guardar ruta
                Button(
                    onClick = {
                        //la idea es que lleve a la lista de rutas, cuando este creada
                        navController.navigate("homeScreen")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Ver rutas",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun LocationCard(title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun RouteDetailItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = "$label:",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(160.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            textAlign = TextAlign.Start
        )
    }
}
