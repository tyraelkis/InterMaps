@file:Suppress("PreviewAnnotationInFunctionWithParameters")

package uji.es.intermaps.View.interestPlace

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun InterestPlaceList(auth: FirebaseAuth, navController: NavController, viewModel: InterestPlaceViewModel) {
    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val interestPlaceService = InterestPlaceService(repository)
    var allPlaces by remember { mutableStateOf<List<InterestPlace>>(emptyList()) }
    val emailPrefix = user?.email?.substringBefore("@") ?: "Usuario"

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

    val favList by remember(allPlaces) { derivedStateOf { allPlaces.filter { it.fav } } }
    val noFavList by remember(allPlaces) { derivedStateOf { allPlaces.filter { !it.fav } } }

    Column(
        modifier = Modifier
                .fillMaxSize()
            .background(
                Color.White
            )
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp)
        ) {
            Image(
                painter = painterResource(
                    id = R.drawable.icono_cerrar
                ),
                contentDescription = "",
                modifier = Modifier
                    .size(25.dp)
                    .clickable { navController.navigate("mainMenu") },
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                        append("Lista de lugares de\n")
                        append(emailPrefix)
                },
                color = Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(lineHeight = 35.sp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Absolute.Left,
            verticalAlignment = Alignment.CenterVertically

        ) {
            Text(
                text = "Tus lugares favoritos",
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
                favList.forEach { place ->

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
                                .size(30.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (viewModel.deleteFavInterestPlace(place.coordinate)) {
                                            withContext(Dispatchers.Main) {
                                                allPlaces = allPlaces.map {
                                                    if (it == place) it.copy(fav = false) else it
                                                }
                                            }
                                        }
                                    }
                                },
                            tint = Color(color = 0XFF007E70)
                        )
                        if(place.alias.isEmpty()) {
                            Text(
                                text = place.toponym,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp)
                            )
                        }
                        else {
                            Text(
                                text = place.alias,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 15.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    viewModel.updateInterestPlace(place)
                                    val encodedToponym = URLEncoder.encode(place.toponym, StandardCharsets.UTF_8.toString())
                                    navController.navigate("interestPlaceSetAlias/$encodedToponym")
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
                text = "Lugares de interés",
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
                noFavList.forEach { notFavPlace ->
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
                                .size(30.dp)
                                .clickable {
                                    CoroutineScope(Dispatchers.IO).launch {
                                        if (viewModel.setFavInterestPlace(notFavPlace.coordinate)) {
                                            withContext(Dispatchers.Main) {
                                                allPlaces = allPlaces.map {
                                                    if (it == notFavPlace) it.copy(fav = true) else it
                                                }
                                            }
                                        }
                                    }
                                },
                            tint = Black
                        )
                        if(notFavPlace.alias.isEmpty()) {
                            Text(
                                text = notFavPlace.toponym,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )
                        } else {
                            Text(
                                text = notFavPlace.alias,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp)
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",

                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    viewModel.updateInterestPlace(notFavPlace)
                                    val encodedToponym = URLEncoder.encode(notFavPlace.toponym, StandardCharsets.UTF_8.toString())
                                    navController.navigate("interestPlaceSetAlias/$encodedToponym")
                                },
                            tint = Black
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            onClick = {
                navController.navigate("interestPlaceCreationByToponym")
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
                text = "Añadir lugar por topónimo",
                fontSize = 20.sp,
            )
        }
        Spacer(modifier = Modifier.height(30.dp))

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
