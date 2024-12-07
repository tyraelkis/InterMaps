package uji.es.intermaps.View.interestPlace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.UserViewModel

@Composable
fun DeleteInterestPlacePopUp (
    viewModel: InterestPlaceViewModel,
    navController: NavController
){

    val coord = viewModel.coordinate
    val showDialog = viewModel.showDeleteDialog.value

    if (showDialog){
        Dialog(
            onDismissRequest = {viewModel.hideDeleteInterestPlacePopUp()}
        ){Column(
                modifier = Modifier
                    .width(350.dp)
                    .background(Color(0XFF007E70), shape = RoundedCornerShape(10.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Eliminar lugar de interes",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.hideDeleteInterestPlacePopUp() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        modifier = Modifier.weight(1f)
                            .padding(start = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    val coroutineScope = rememberCoroutineScope()

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                viewModel.deleteInterestPlace(coord)
                                viewModel.hideDeleteInterestPlacePopUp()
                                navController.navigate("interestPlaceList")}
                                  },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        modifier = Modifier.weight(1f)
                            .padding(end = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "Eliminar",
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            Spacer(modifier = Modifier.height(20.dp))
            }

        }

    }
}