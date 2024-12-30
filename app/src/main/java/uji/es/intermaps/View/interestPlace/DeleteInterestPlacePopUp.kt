package uji.es.intermaps.View.interestPlace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uji.es.intermaps.ViewModel.InterestPlaceViewModel

@Composable
fun DeleteInterestPlacePopUp (viewModel: InterestPlaceViewModel, navController: NavController){
    val coord = viewModel.coordinate
    val showDialog = viewModel.showDeleteDialog.value

    if (showDialog){
        Dialog(
            onDismissRequest = {viewModel.hideDeleteInterestPlacePopUp()}
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(vertical = 300.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0XFF007E70))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "¿Estas seguro de eliminar la ruta?",
                        color = White,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 36.sp
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.hideDeleteInterestPlacePopUp()
                            },
                            modifier = Modifier.weight(1f)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "No", fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        val coroutineScope = rememberCoroutineScope()
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteInterestPlace(coord)
                                    viewModel.hideDeleteInterestPlacePopUp()
                                    if (viewModel.getErrorMessage() == "")
                                        navController.navigate("interestPlaceList")
                                }
                            },
                            modifier = Modifier.weight(1f)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(text = "Sí", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}