package uji.es.intermaps.View.home

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.InterestPlaceService

@Composable
fun MainMenu() {

    var busqueda by remember { mutableStateOf("") }
    var interestPlace by remember { mutableStateOf(InterestPlace()) }
    var showMenu by remember { mutableStateOf(false) }
    val interestPlaceService = InterestPlaceService(FirebaseRepository())
    //val user = FirebaseRepository().auth.currentUser

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
                                    try {
                                        val aBuscar = busqueda.split(",")
                                        coordinate =
                                            Coordinate(aBuscar[0].toDouble(), aBuscar[1].toDouble())
                                        busquedaCoordenadas = true
                                    } catch (e: Exception) {
                                        Log.e("ERROR_CONVERSION", "Error al convertir coordenadas")
                                    }
                                    //TODO falta implementar la vista del lugar que se ha buscado
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (busquedaCoordenadas) {
                                            try {
                                                interestPlace =
                                                    interestPlaceService.searchInterestPlaceByCoordiante(
                                                        coordinate
                                                    )
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "ERROR_BUSQUEDA_COORDS",
                                                    "Error al buscar por coordenadas"
                                                )
                                            }
                                        } else {
                                            try {
                                                //interestPlace = interestPlaceService.searchInterestPlaceByToponym(busqueda)
                                            } catch (e: Exception) {
                                                Log.e(
                                                    "ERROR_BUSQUEDA_TOP",
                                                    "Error al buscar por topónimo"
                                                )
                                            }
                                        }
                                    }
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
                mapViewportState = rememberMapViewportState {
                    setCameraOptions {
                        zoom(12.0)
                        center(Point.fromLngLat(-0.04935, 39.98567))
                        pitch(0.0)
                        bearing(0.0)
                    }
                },
            )
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
                    ),

                ) {

                Spacer(modifier = Modifier.height(20.dp))

                Row (
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ){//Botón para cerrar el menú y correo usuario

                    Spacer(modifier = Modifier.width(10.dp))

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
                        text = "correo@usuario.es",//TODO usar el auth para sacar el correo
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
                            .clickable { showMenu = false }, //TODO Ir a perfil
                        contentScale = ContentScale.Crop
                    )

                    Button(//Texto con enlace
                        onClick = {
                            showMenu = false //TODO Ir a perfil
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
                            .clickable { showMenu = false }, //TODO Ir a lugares
                        contentScale = ContentScale.Crop
                    )

                    Button(//Texto con enlace
                        onClick = {
                            showMenu = false //TODO Ir a lugares
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
    }
}
