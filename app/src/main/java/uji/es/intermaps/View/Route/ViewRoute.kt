package uji.es.intermaps.View.Route

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.ViewModel.RouteViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import uji.es.intermaps.R

@Composable
fun ViewRoute(navController: NavController, viewModel: RouteViewModel) {
    val route = viewModel.route.value
    val origin = route?.origin
    val destination = route?.destination
    val transportMethod = route?.transportMethod
    val routeType = route?.routeType
    val routePoints = route?.route
    val distance = route?.distance
    val duration = route?.duration
    val cost = route?.cost
    val vehiclePlate = route?.vehiclePlate
    val loading = viewModel.loading
    val routeInDataBase = viewModel.routeInDataBase
    val startPoint:Coordinate? = routePoints?.getOrNull(0)
    val endPoint:Coordinate? = routePoints?.getOrNull(routePoints.size - 1)
    val startLong = startPoint?.longitude ?: 0.0
    val startLat = startPoint?.latitude ?: 0.0
    val endLong = endPoint?.longitude ?: 0.0
    val endLat = endPoint?.latitude ?: 0.0


    if (loading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) { CircularProgressIndicator() }
    }else {
        val pointsToMap = routePoints!!.map { coordinate ->
            fromLngLat(coordinate.longitude, coordinate.latitude)
        }
        Scaffold { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                //Mapa con la ruta
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {

                    MapboxMap(
                        modifier = Modifier
                            .fillMaxWidth(),
                        mapViewportState = rememberMapViewportState {
                            setCameraOptions {
                                zoom(11.0)
                                center(fromLngLat(startLong, startLat))
                                pitch(0.0)
                                bearing(0.0)
                            }
                        }
                    ) {
                        PolylineAnnotation(
                            points = pointsToMap,
                        )
                        {
                            lineColor = Color(color = 0XFF007E70)
                            lineWidth = 5.0
                        }

                        //Marcador inicial
                        ViewAnnotation(
                            options = viewAnnotationOptions {
                                geometry(fromLngLat(startLong, startLat))
                                allowOverlap(true)
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.marker_icon),
                                contentDescription = "Inicio de la ruta",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        ViewAnnotation(
                            options = viewAnnotationOptions {
                                geometry(fromLngLat(endLong, endLat))
                                allowOverlap(true)
                            }
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.marker_icon),
                                contentDescription = "Fin de la ruta",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Información de la ruta
                Text(
                    text = "${origin} → ${destination}",
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

                Spacer(modifier = Modifier.height(4.dp))

                Column(modifier = Modifier.padding(16.dp)) {
                    RouteDetailItem(label = "Método de transporte", value = "${transportMethod}")
                    if (transportMethod == TransportMethods.VEHICULO) {
                        RouteDetailItem(label = "Vehículo", value = "${vehiclePlate}")
                    }
                    RouteDetailItem(label = "Tipo de ruta", value = "${routeType}")
                    RouteDetailItem(label = "Distancia", value = "${distance} km")
                    RouteDetailItem(label = "Duración", value = "${duration}")

                    if (transportMethod == TransportMethods.VEHICULO){
                        RouteDetailItem(label = "Coste", value = "${cost} €")
                    }
                    else{
                        RouteDetailItem(label = "Coste", value = "${cost} kcal")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (routeInDataBase) {
                    Button(
                        onClick = {
                            viewModel.route.value?.let { currentRoute ->
                                viewModel.deleteRoute(route)
                            }
                            viewModel.updateRouteList()
                            navController.navigate("routeList")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Eliminar ruta",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                    }
                } else{
                // Botón Guardar ruta
                Button(
                    onClick = {
                        viewModel.route.value?.let { currentRoute ->
                            viewModel.saveRoute(currentRoute)
                        }
                        viewModel.updateRouteList()
                        navController.navigate("routeList")
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Guardar ruta",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
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
