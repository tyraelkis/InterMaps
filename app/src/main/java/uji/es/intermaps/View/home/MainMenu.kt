package uji.es.intermaps.View.home

import android.util.Log
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.ViewAnnotation
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import com.mapbox.maps.viewannotation.geometry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.ViewModel.InterestPlaceViewModel

@Composable
fun MainMenu(auth: FirebaseAuth, navController: NavController, viewModel: InterestPlaceViewModel) {

    var busqueda by remember { mutableStateOf("") }
    var interestPlace by remember { mutableStateOf(InterestPlace(Coordinate(39.98567, -0.04935), "","",false)) }
    var showMenu by remember { mutableStateOf(false) }
    var showPlaceData by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val interestPlaceService = InterestPlaceService(FirebaseRepository())
    var email = auth.currentUser?.email

    if (email == null)
        email = "no hay sesión"

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(12.0)
            center(Point.fromLngLat(-0.0675, 39.9947))
            pitch(0.0)
            bearing(0.0)
        }
    }

    var clickedPoint by remember { mutableStateOf<Point?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    White
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Row( //Rectangulo verde
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(Color(0XFF007E70)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row( //Barra de busqueda
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .border(
                            width = 1.dp,
                            color = Color(0xFF92C1C1),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .background(
                            color = Color(0xFF92C1C1),
                            shape = RoundedCornerShape(25.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(//Caja que superpone los dos textos
                        modifier = Modifier.weight(1f)
                    ) {
                        if (busqueda.isEmpty()) { //Mete texto mientras no se escriba
                            Text(//Texto si no escribes nada
                                text = "Coordenadas/Topónimo",
                                color = Black,
                                fontSize = 16.sp,
                                modifier = Modifier.offset(y = (-3).dp)
                            )
                        }
                        BasicTextField(//Texto de busqueda
                            value = busqueda,
                            onValueChange = { busqueda = it },
                            textStyle = TextStyle(
                                fontSize = 16.sp,
                                color = Black
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Image(//Imagen lupa (si clicas hace la busqueda)
                        painter = painterResource(
                            id = R.drawable.leading_icon
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(50.dp)
                            .clickable {
                                if (busqueda.isNotEmpty()) {
                                    //TODO mover la comprovación de coordenadas a un servicio
                                    var coordinate = Coordinate()
                                    var busquedaCoordenadas = false
                                    val coordinatePattern = Regex("""^\s*(-?\d+(\.\d+)?)\s*,\s*(-?\d+(\.\d+)?)\s*$""")
                                    val input = busqueda.trim()
                                    if (coordinatePattern.matches(input)) {
                                        val parts = input.split(",")
                                        val lat = parts[0].trim().toDouble()
                                        val lon = parts[1].trim().toDouble()
                                        coordinate = Coordinate(lat, lon)
                                        busquedaCoordenadas = true
                                        errorMessage = ""
                                    } else {
                                        Log.e("FORMATO_NO_VALIDO", "El input no tiene formato de coordenadas")
                                    }
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (busquedaCoordenadas) {
                                            try {
                                                interestPlace = interestPlaceService.searchInterestPlaceByCoordiante(coordinate)
                                                errorMessage=""
                                                busqueda = ""
                                                mapViewportState.setCameraOptions {
                                                    center(Point.fromLngLat(interestPlace.coordinate.longitude, interestPlace.coordinate.latitude))
                                                    zoom(12.0)
                                                }
                                                clickedPoint = Point.fromLngLat(interestPlace.coordinate.longitude, interestPlace.coordinate.latitude)
                                            } catch (e: NotValidCoordinatesException) {
                                                Log.e("ERROR_BUSQUEDA_COORDS", "Error al buscar por coordenadas")
                                                errorMessage = e.message.toString()
                                                busqueda = ""
                                            } catch (e: Exception) {
                                                Log.e("ERROR_BUSQUEDA_COORDS", "Error al buscar por coordenadas")
                                                errorMessage = e.message.toString()
                                                busqueda = ""
                                            }
                                        } else {
                                            try {
                                                interestPlace = interestPlaceService.searchInterestPlaceByToponym(input)
                                                errorMessage=""
                                                busqueda = ""
                                                mapViewportState.setCameraOptions {
                                                    center(Point.fromLngLat(interestPlace.coordinate.longitude, interestPlace.coordinate.latitude))
                                                    zoom(12.0)
                                                }
                                                clickedPoint = Point.fromLngLat(interestPlace.coordinate.longitude, interestPlace.coordinate.latitude)
                                            } catch (e: NotSuchPlaceException) {
                                                Log.e("ERROR_BUSQUEDA_TOP", "Error al buscar por topónimo")
                                                errorMessage = e.message.toString()
                                                busqueda = ""
                                            } catch (e: Exception) {
                                                Log.e("ERROR_BUSQUEDA_TOP", "Error al buscar por topónimo")
                                                errorMessage = e.message.toString()
                                                busqueda = ""
                                            }
                                        }
                                    }
                                    showPlaceData = true
                                }
                                else {
                                    showPlaceData = false
                                }
                            },
                        contentScale = ContentScale.Crop
                    )
                }
                Image(//Imagen barras menu
                    painter = painterResource(
                        id = R.drawable.barras_menu
                    ),
                    contentDescription = "",
                    modifier = Modifier.size(20.dp)
                        .clickable { showMenu = true },
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
            }

            MapboxMap(//Mapa
                Modifier.fillMaxSize(),
                mapViewportState = mapViewportState,
                onMapClickListener = { point ->
                    clickedPoint = point
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            interestPlace =
                                interestPlaceService.searchInterestPlaceByCoordiante(
                                    Coordinate(point.latitude(), point.longitude())
                                )
                            errorMessage = ""
                        } catch (e: NotValidCoordinatesException) {
                            Log.e(
                                "ERROR_COORDS_CLICK",
                                "Error al sacar las coordenadas del mapa"
                            )
                            errorMessage = e.message.toString()
                        } catch (e: Exception) {
                            Log.e(
                                "ERROR_COORDS_CLICK",
                                "Error al sacar las coordenadas del mapa"
                            )
                            errorMessage = e.message.toString()
                        }
                    }
                    showPlaceData = true
                    true
                }
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
        if (showMenu) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 20.dp,
                        top = 150.dp,
                        end = 20.dp,
                        bottom = 250.dp)
                    .background(
                        Color(0XFF007E70),
                        shape = RoundedCornerShape(15.dp)
                    )
                ) {

                Spacer(modifier = Modifier.height(20.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){//Botón para cerrar el menú y correo usuario

                    Spacer(modifier = Modifier.width(20.dp))

                    Image(//Imagen cerrar
                        painter = painterResource(
                            id = R.drawable.icono_cerrar
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(20.dp)
                            .clickable { showMenu = false },
                        contentScale = ContentScale.Crop
                    )

                    Text(
                        text = email,
                        color = Black,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset(x = (-10).dp)
                            .padding(horizontal = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){//Icono perfil y texto con enlace

                    Spacer(modifier = Modifier.width(40.dp))

                    Image(//Imagen icono perfil
                        painter = painterResource(
                            id = R.drawable.user_logo_black
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                            .clickable { navController.navigate("userDataScreen") }, //TODO Comprobar que va bien
                        contentScale = ContentScale.Crop
                    )

                    Button(//Texto con enlace
                        onClick = {
                            navController.navigate("userDataScreen") //TODO Comprobar que va bien
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0X00000000)),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = "Tu perfil", color = Black, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){//Icono lugares y texto con enlace

                    Spacer(modifier = Modifier.width(40.dp))

                    Image(//Imagen icono lugares
                        painter = painterResource(
                            id = R.drawable.places_icon
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                            .clickable { navController.navigate("interestPlaceList") }, //TODO Comprobar que va bien
                        contentScale = ContentScale.Crop
                    )

                    Button(//Texto con enlace
                        onClick = {
                            navController.navigate("interestPlaceList") //TODO Comprobar que va bien
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0X00000000)),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = "Tus lugares", color = Black, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){//Icono vehículos y texto con enlace

                    Spacer(modifier = Modifier.width(40.dp))

                    Image(//Imagen icono vehículos
                        painter = painterResource(
                            id = R.drawable.vehicles_icon
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                            .clickable { showMenu = false }, //TODO Ir a vehículos
                        contentScale = ContentScale.Crop
                    )

                    Button(//Texto con enlace
                        onClick = {
                            showMenu = false //TODO Ir a vehículos
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0X00000000)),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = "Tus vehículos", color = Black, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){//Icono rutas y texto con enlace

                    Spacer(modifier = Modifier.width(40.dp))

                    Image(//Imagen icono rutas
                        painter = painterResource(
                            id = R.drawable.route_icon
                        ),
                        contentDescription = "",
                        modifier = Modifier.size(40.dp)
                            .clickable { showMenu = false }, //TODO Ir a rutas
                        contentScale = ContentScale.Crop
                    )

                    Button(//Texto con enlace
                        onClick = {
                            showMenu = false //TODO Ir a rutas
                        },
                        modifier = Modifier
                            .height(40.dp)
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0X00000000)),
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(text = "Tus rutas", color = Black, fontSize = 18.sp)
                        }
                    }
                }
            }
        }

        if (showPlaceData){
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 0.dp,
                        top = 500.dp,
                        end = 0.dp,
                        bottom = 0.dp)
                    .background(
                        White,
                    ),
                //verticalArrangement = Arrangement.Center, // Centra el contenido verticalmente
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (errorMessage.isEmpty()) { //Si no hay errores en la busqueda se muestran los datos

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = interestPlace.toponym,
                            color = Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        Spacer(modifier = Modifier.width(10.dp))

                        Text(
                            text = "(${interestPlace.coordinate.latitude}, ${interestPlace.coordinate.longitude})",
                            color = Gray,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.width(20.dp))

                        Image(//Imagen lugar 1 //TODO falta centrar las fotos y linkear el botón
                            painter = painterResource(
                                id = R.drawable.not_aviable_image
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(160.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(20.dp))

                        Image(//Imagen lugar 2
                            painter = painterResource(
                                id = R.drawable.not_aviable_image
                            ),
                            contentDescription = "",
                            modifier = Modifier.size(160.dp),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(20.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(//Botón añadir lugar
                            onClick = {
                                viewModel.setInterestPlace(interestPlace)
                                navController.navigate("addInterestPlace")
                            },
                            modifier = Modifier
                                .height(40.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF000000)),
                        ) {
                            Text(text = "Añadir lugar", color = White, fontSize = 18.sp)
                        }
                    }
                }
                else { //Si ha habido errores se muestra el mensaje de error
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(White),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = errorMessage,
                            color = Red,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
