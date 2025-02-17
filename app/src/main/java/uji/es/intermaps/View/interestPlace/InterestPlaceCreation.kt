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
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.geometry
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.InterestPlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestPlaceCreation(navController: NavController, viewModel: InterestPlaceViewModel){
    val place = viewModel.selectedInterestPlace
    var alias by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    CreateInterestPlaceCorrectPopUp(viewModel, navController)
    CreateInterestPlaceErrorPopUp(viewModel, navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                White
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
                text = place.toponym,
                color = Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${place.coordinate.latitude}, ${place.coordinate.longitude}",
                color = Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Column(
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {

            MapboxMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                mapViewportState = rememberMapViewportState {
                    setCameraOptions {
                        zoom(12.0)
                        center(
                            Point.fromLngLat(
                                place.coordinate.longitude,
                                place.coordinate.latitude
                            )
                        )
                        pitch(0.0)
                        bearing(0.0)
                    }
                }
            ) {
                ViewAnnotation(
                    options = viewAnnotationOptions {
                        geometry(Point.fromLngLat(place.coordinate.longitude, place.coordinate.latitude))
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

        Spacer(modifier = Modifier.height(32.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Alias",
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
            // Texto en el fondo
            TextField(
                value = alias,
                onValueChange = { alias = it },
                modifier = Modifier
                    .height(52.dp)
                    .fillMaxWidth()
                    .border(1.dp, Black, RoundedCornerShape(10.dp)),
                placeholder = { Text("Añade un nuevo alias a este lugar", style = TextStyle(fontSize = 20.sp)) },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color(0xFFFFFFFF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.createInterestPlace(place.coordinate, place.toponym, alias)
                    withContext(Dispatchers.Main) {
                        errorMessage = viewModel.getErrorMessage()
                        if (errorMessage.isEmpty())
                            viewModel.showCreateInterestPlaceCorrectPopUp()
                        else
                            viewModel.showCreateInterestPlaceErrorPopUp()
                    }
                }
            },
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp, vertical = 1.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Guardar lugar", color = White, fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}