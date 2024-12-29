@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package uji.es.intermaps.View.Route

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.RouteService
import uji.es.intermaps.ViewModel.RouteViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun RouteList(auth: FirebaseAuth, navController: NavController, viewModel: RouteViewModel) {
    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val allRoutes by viewModel.routes.observeAsState(emptyList())
    val emailPrefix = user?.email?.substringBefore("@") ?: "Usuario"


    viewModel.updateRouteList()
    Log.i("Lista de rutas", allRoutes.toString() )

    val favList = allRoutes.filter { it.fav }
    Log.i("Lista de rutas", allRoutes.toString() )

    val noFavList = allRoutes.filter { !it.fav }
    Log.i("Lista de rutas", allRoutes.toString() )


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
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
                text = buildAnnotatedString {
                    append("Lista de rutas de\n")
                    append("\n")
                    append(emailPrefix)
                },
                color = Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Tus rutas favoritas",
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
                favList.forEach { route ->

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
                                .size(30.dp),
                            tint = Color(color = 0XFF007E70)
                        )
                        Text(
                            text = route.origin + " --> "+ route.destination,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ver",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    viewModel.getRoute(route.origin, route.destination, route.transportMethod, route.vehiclePlate)
                                    navController.navigate(
                                        "viewRoute/" +
                                                URLEncoder.encode(route.origin, StandardCharsets.UTF_8.toString()) + "/" +
                                                URLEncoder.encode(route.destination, StandardCharsets.UTF_8.toString()) + "/" +
                                                URLEncoder.encode(route.transportMethod.name, StandardCharsets.UTF_8.toString()) + "/" +
                                                URLEncoder.encode(route.vehiclePlate, StandardCharsets.UTF_8.toString())
                                    )

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
                text = "Rutas ",
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
                noFavList.forEach { notFavRoute ->
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
                                .size(30.dp),
                            tint = Black
                        )

                        Text(
                            text = notFavRoute.origin + " --> "+ notFavRoute.destination,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 8.dp)
                        )

                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ver",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {

                                    viewModel.getRoute(notFavRoute.origin, notFavRoute.destination, notFavRoute.transportMethod, notFavRoute.vehiclePlate)
                                    navController.navigate(
                                        "viewRoute/" +
                                                URLEncoder.encode(notFavRoute.origin, StandardCharsets.UTF_8.toString()) + "/" +
                                                URLEncoder.encode(notFavRoute.destination, StandardCharsets.UTF_8.toString()) + "/" +
                                                URLEncoder.encode(notFavRoute.transportMethod.name, StandardCharsets.UTF_8.toString()) + "/" +
                                                URLEncoder.encode(notFavRoute.vehiclePlate, StandardCharsets.UTF_8.toString())
                                    ) //val encodedToponym = URLEncoder.encode(notFavPlace.toponym, StandardCharsets.UTF_8.toString())
                                    //navController.navigate("interestPlaceSetAlias/$encodedToponym")
                                },
                            tint = Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                navController.navigate("createNewRoute")
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

        Spacer(modifier = Modifier.height(30.dp))
    }
}