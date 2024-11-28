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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import uji.es.intermaps.Model.FirebaseRepository
import uji.es.intermaps.Model.InterestPlaceService
import uji.es.intermaps.Model.Repository
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.InterestPlaceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestPlaceCreation(viewModel: InterestPlaceViewModel){
    val place = viewModel.interestPlace.observeAsState().value ?:return
    var showPopupCreateSucces by remember { mutableStateOf(false) }
    var showPopupCreateError by remember { mutableStateOf(false) }
    var alias by remember { mutableStateOf("") }
    var repository: Repository = FirebaseRepository()
    var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    val  coroutineScope = rememberCoroutineScope()
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
                text = "${place.coordinate.latitude}, ${place.coordinate.longitude}",
                color = Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(15.dp))

        Box(
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(20.dp))
        ) {

            Image(
                painter = painterResource(
                    id = R.drawable.not_aviable_image
                ),
                contentDescription = "",
                modifier = Modifier
                    .width(350.dp)
                    .height(350.dp),
                contentScale = ContentScale.Crop
            )
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
                text = "Toponym",
                color = Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .height(52.dp)
                .width(350.dp)
                .background(Color(0xFFFFFFF), shape = RoundedCornerShape(10.dp))
                .border(
                    width = 1.dp,
                    color = Black,
                    shape = RoundedCornerShape(10.dp) )
                .clip(RoundedCornerShape(10.dp))
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            // Texto en el fondo
            Text(
                text = "${place.toponym}",
                color = Black,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterStart),
            )
        }
        Spacer(modifier = Modifier.height(20.dp))

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
                    containerColor = Color(0xFFFFFFF),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                coroutineScope.launch{
                }
            } ,
            modifier = Modifier
                .height(36.dp)
                .padding(horizontal = 32.dp, vertical = 1.dp)
                .align(AbsoluteAlignment.Right),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Añadir", color = White, fontSize = 14.sp)
        }
    }

    if (showPopupCreateSucces) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .background(Color(0x80FFFFFF))
                .clickable { showPopupCreateSucces = false},
            contentAlignment = Alignment.Center
        ) {
            // Popup con el contenido de edición de correo
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
                                showPopupCreateSucces = false
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

    if (showPopupCreateError) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .background(Color(0x80FFFFFF))
                .clickable { showPopupCreateSucces = false},
            contentAlignment = Alignment.Center
        ) {
            // Popup con el contenido de edición de correo
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
                        text = "No no se ha podido añadir el lugar de interés",
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
                                showPopupCreateError = false
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