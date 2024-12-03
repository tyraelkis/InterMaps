package uji.es.intermaps.View.user

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import uji.es.intermaps.ViewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordUserPopUp (
    viewModel: UserViewModel,
    navController: NavController
){

    val showDialog = viewModel.showPasswordDialog.value
    val newPassword = viewModel.newPassword
    val confirmPassword = viewModel.confirmPassword
    val errorMessage = viewModel.errorMessage


    if (showDialog){
        Dialog(
            onDismissRequest = {viewModel.hidePasswordPopUp()}
        ) {
            Column(
                modifier = Modifier
                    .width(350.dp)
                    .background(Color(0XFF007E70), shape = RoundedCornerShape(10.dp)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Cambio de contraseña",
                    color = Color.White,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(32.dp))

                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Nueva contraseña",
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = newPassword,
                        onValueChange = { viewModel.newPassword = it},
                        modifier = Modifier
                            .height(52.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        placeholder = { Text("Introduce la nueva contraseña") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFF80BEB7),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))

                // Confirmación de contraseña
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = "Confirmación de contraseña",
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    TextField(
                        value = confirmPassword,
                        onValueChange = { viewModel.confirmPassword = it },
                        modifier = Modifier
                            .height(52.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        placeholder = { Text("Confirma la nueva contraseña") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color(0xFF80BEB7),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.White,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            viewModel.changeUserPassword()
                        },
                        modifier = Modifier.weight(1f)
                            .padding(start = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = "Aceptar",
                            fontSize = 16.sp,
                        )
                    }


                    Button(
                        onClick = { viewModel.hidePasswordPopUp() },
                        modifier = Modifier.weight(1f)
                            .padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                    ) {
                        Text(
                            text = "Cancelar",
                            fontSize = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))

            }
        }

    }
}