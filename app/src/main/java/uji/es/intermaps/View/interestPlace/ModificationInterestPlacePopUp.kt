package uji.es.intermaps.View.interestPlace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.UserViewModel

@Composable
fun ModificationInterestPlacePopUp (
    viewModel: InterestPlaceViewModel,
){

    val showDialog = viewModel.showUpdateDialog.value
    if (showDialog) {
        Dialog(
            onDismissRequest = { viewModel.hideUpdateInterestPlacePopUp() }
        ) {
            Column(
                modifier = Modifier
                    .width(350.dp)
                    .background(Color(0XFF007E70), shape = RoundedCornerShape(10.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "El cambio de alias se ha realizado correctamente",
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    style = androidx.compose.ui.text.TextStyle(
                        lineHeight = 28.sp
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.hideUpdateInterestPlacePopUp() },
                    modifier = Modifier,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "Aceptar",
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}