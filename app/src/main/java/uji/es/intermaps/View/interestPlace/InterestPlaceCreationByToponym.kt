package uji.es.intermaps.View.interestPlace

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import kotlinx.coroutines.launch
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import com.mapbox.geojson.Point.fromLngLat
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uji.es.intermaps.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestPlaceCreationByToponym(navController: NavController, viewModel: InterestPlaceViewModel) {
    var showPopupCreateSucces by remember { mutableStateOf(false) }
    var toponym by remember { mutableStateOf("") }
    var selectToponym by remember { mutableStateOf("") }
    var sePuedeAñadir by remember { mutableStateOf(false) }


    CreateInterestPlaceCorrectPopUp(viewModel)

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(11.0)
            center(fromLngLat(-0.0675, 39.9947)) // Coordenadas iniciales
            pitch(0.0)
            bearing(0.0)
        }
    }

    var clickedPoint by remember { mutableStateOf<Point?>(null) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Column(
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {

            MapboxMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Ocupa la mitad del espacio
                mapViewportState = mapViewportState
            ) {
                clickedPoint?.let { point ->
                    ViewAnnotation(
                        options = viewAnnotationOptions {
                            geometry(point)
                            allowOverlap(true)
                        }
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.marker_icon),
                            contentDescription = "Marker",
                            modifier = Modifier.size(40.dp)
                        )
                    }
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
                text = "Topónimo",
                color = Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .height(52.dp)
                .width(350.dp)
        ) {
            TextField(
                value = toponym,
                onValueChange = { toponym = it },
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
                    .border(1.dp, Black, RoundedCornerShape(10.dp)),
                placeholder = { Text("Escribe un topónimo", style = TextStyle(fontSize = 20.sp)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFFFFF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )

        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Absolute.Right,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (sePuedeAñadir) {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.createInterestPlaceFromToponym(selectToponym)
                            var errorMessage = viewModel.getErrorMessage()
                            withContext(Dispatchers.Main) {
                                if (errorMessage.isEmpty())
                                    showPopupCreateSucces = true
                                else
                                    viewModel.showCreateInterestPlaceErrorPopUp()
                            }
                        }
                    },
                    modifier = Modifier
                        .height(36.dp)
                        .padding(horizontal = 32.dp, vertical = 1.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Añadir lugar", color = White, fontSize = 14.sp)
                }
            }

            Button(
                onClick = {
                    viewModel.getLocationsByToponim(toponym)
                },
                modifier = Modifier
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Black),
                shape = RoundedCornerShape(10.dp),

                ) {
                Text(text = "Obtener lugares", color = White, fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            LazyColumn {
                items(viewModel.locations.value.size) { index ->
                    Text(text = viewModel.locations.value[index].properties.label,
                        modifier = Modifier
                            .clickable {
                                sePuedeAñadir = true
                                selectToponym = viewModel.locations.value[index].properties.label
                                val coordinates =
                                    viewModel.locations.value[index].geometry.coordinates
                                mapViewportState.setCameraOptions {
                                    zoom(11.0)
                                    center(
                                        fromLngLat(
                                            coordinates[0],
                                            coordinates[1]
                                        )
                                    )
                                }
                                clickedPoint = fromLngLat(
                                    coordinates[0],
                                    coordinates[1]
                                ) //Establece la ubicación del marcador
                            }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    if (showPopupCreateSucces) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .background(Color(0x80FFFFFF)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .height(250.dp)
                    .width(395.dp)
                    .background(Color(0XFF007E70), shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "El lugar de interés se ha añadido correctamente",
                        color = White,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                navController.navigate("interestPlaceList")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Black
                            )
                        ) {
                            Text(
                                text = "Aceptar",
                                fontSize = 16.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}