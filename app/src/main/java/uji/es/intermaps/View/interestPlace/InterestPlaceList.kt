package uji.es.intermaps.View.interestPlace

import android.util.Log
import com.google.firebase.auth.FirebaseAuth

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.gson.Gson
import uji.es.intermaps.Model.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.InterestPlaceService
import uji.es.intermaps.Model.Repository
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.InterestPlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestPlaceList(navigateToInterestPlaceList: () -> Unit = {}, auth: FirebaseAuth, navigateToInterestPlaceSetAlias: () -> Unit, viewModel: InterestPlaceViewModel) {
    var db = FirebaseFirestore.getInstance()
    var user = auth.currentUser
    var repository: Repository = FirebaseRepository()
    var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    var interestPlace: InterestPlace = InterestPlace()
    var favList by remember { mutableStateOf<List<InterestPlace>>(emptyList()) }
    var noFavList by remember { mutableStateOf<List<InterestPlace>>(emptyList()) }

    LaunchedEffect(Unit) {
        interestPlaceService.getFavList { places ->
            favList = places
        }
        interestPlaceService.getNoFavList { NoFavplaces ->
            noFavList = NoFavplaces
        }
    }
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
                text = "Lista de lugares de ${user?.email}",
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
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
                text = "Tus lugares favoritos",
                color = Color.Black,
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
                                .size(30.dp),
                            tint = Color(color = 0XFF007E70)
                        )
                        Text(
                            text = place.toponym,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp)

                        )
                        Button(
                            onClick = {
                                viewModel.setInterestPlace(place)
                                navigateToInterestPlaceSetAlias()
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Black
                            )
                        ) {
                            Text(
                                text = "Editar",
                                fontSize = 16.sp,
                            )
                        }

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
                color = Color.Black,
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
                                .size(30.dp),
                            tint = Color.Black
                        )
                        Text(
                            text = notFavPlace.toponym,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 15.dp)
                        )
                        Button(
                            onClick = {
                                viewModel.setInterestPlace(notFavPlace)
                                navigateToInterestPlaceSetAlias()
                            },
                            modifier = Modifier
                                .width(100.dp)
                                .height(42.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Black
                            )
                        ) {
                            Text(
                                text = "Editar",
                                fontSize = 16.sp,
                            )
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))

                }
            }
        }

    }
}