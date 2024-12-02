package uji.es.intermaps.View.user

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uji.es.intermaps.Exceptions.NotValidUserData
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.ViewModel.UserService
import uji.es.intermaps.R
import uji.es.intermaps.ViewModel.UserViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreen(auth: FirebaseAuth, navController: NavController, viewModel: UserViewModel) {

    var expandedVehicles by remember { mutableStateOf(false) }
    var expandedRoutes by remember { mutableStateOf(false) }
    var expandedTransport by remember { mutableStateOf(false) }
    val optionsVehicles = listOf("...", "Gasolina", "Diesel", "Eléctrico")
    val optionsRoutes = listOf("...", "Rápida", "Corta", "Económica")
    val optionsTransport = listOf("...", "En coche", "A pie", "En bicicleta")
    var selectedOptionVehicles by remember { mutableStateOf(optionsVehicles[0]) }
    var selectedOptionRoutes by remember { mutableStateOf(optionsRoutes[0]) }
    var selectedOptionTransport by remember { mutableStateOf(optionsRoutes[0]) }

    var errorMessage by remember { mutableStateOf("") }

    val user = auth.currentUser
    val repository: Repository = FirebaseRepository()
    val userService = UserService(repository)
    var currentEmail by remember { mutableStateOf(user?.email.toString()) }

    DeleteUserPopUp(viewModel, navController)
    ModificationUserPopUp(viewModel)
    PasswordUserPopUp(viewModel, navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color.White),
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
            Image(
                painter = painterResource(
                    id = R.drawable.user_logo
                ),
                contentDescription = "",
                modifier = Modifier
                    .width(40.dp)
                    .height(40.dp)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Text(
                text = currentEmail,
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = { viewModel.showPasswordPopUp() },
            modifier = Modifier
                .height(45.dp)
                .fillMaxWidth(1f)
                .padding(horizontal = 32.dp, vertical = 1.dp)
                .align(AbsoluteAlignment.Right),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "Cambiar Contraseña",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(64.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Vehículo pred.",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            ExposedDropdownMenuBox(
                expanded = expandedVehicles,
                onExpandedChange = { expandedVehicles = !expandedVehicles }
            ) {
                TextField(
                    value = selectedOptionVehicles,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                        .width(160.dp)
                        .padding(0.dp)
                        .height(48.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expandedVehicles) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                )
                ExposedDropdownMenu(
                    expanded = expandedVehicles,
                    onDismissRequest = { expandedVehicles = false }
                ) {
                    optionsVehicles.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    color = if (option == selectedOptionVehicles) Color.White else Color.Black
                                )
                            },
                            onClick = {
                                selectedOptionVehicles = option
                                expandedVehicles = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (option == selectedOptionVehicles) Color.Gray else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tipo de ruta pred.",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            ExposedDropdownMenuBox(
                expanded = expandedRoutes,
                onExpandedChange = { expandedRoutes = !expandedRoutes }
            ) {
                TextField(
                    value = selectedOptionRoutes,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                        .width(160.dp)
                        .padding(0.dp)
                        .height(48.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expandedRoutes) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                )
                ExposedDropdownMenu(
                    expanded = expandedRoutes,
                    onDismissRequest = { expandedRoutes = false }
                ) {
                    optionsRoutes.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    color = if (option == selectedOptionRoutes) Color.White else Color.Black
                                )
                            },
                            onClick = {
                                selectedOptionRoutes = option
                                expandedRoutes = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (option == selectedOptionRoutes) Color.Gray else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tipo de ruta pred.",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            ExposedDropdownMenuBox(
                expanded = expandedTransport,
                onExpandedChange = { expandedTransport = !expandedTransport }
            ) {
                TextField(
                    value = selectedOptionTransport,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.menuAnchor()
                        .width(160.dp)
                        .padding(0.dp)
                        .height(48.dp)
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp)),
                    trailingIcon = {
                        Icon(
                            imageVector = if (expandedTransport) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    ),
                )
                ExposedDropdownMenu(
                    expanded = expandedTransport,
                    onDismissRequest = { expandedTransport = false }
                ) {
                    optionsTransport.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    color = if (option == selectedOptionTransport) Color.White else Color.Black
                                )
                            },
                            onClick = {
                                selectedOptionTransport = option
                                expandedTransport = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (option == selectedOptionTransport) Color.Gray else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))

        Button(
            onClick = {},
            modifier = Modifier
                .height(45.dp)
                .width(350.dp)
                .padding(horizontal = 32.dp, vertical = 1.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0XFF808080)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Guardar Datos", color = White, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val success = userService.signOut()
                        withContext(Dispatchers.Main) {
                            if (success) {
                                navController.navigate("initial")
                            } else {
                                errorMessage = "Error al cerrar sesión."
                                Log.e("SignOut", "No se pudo cerrar la sesión.")
                            }
                        }
                    } catch (e: SessionNotStartedException) {
                        withContext(Dispatchers.Main) {
                            errorMessage = "No hay ninguna sesión iniciada."
                            Log.e("SignOut", e.message.toString())
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            errorMessage = "Ocurrió un error inesperado: ${e.message}"
                            Log.e("SignOut", e.message.toString(), e)
                        }
                    }
                }
            },
            modifier = Modifier
                .height(45.dp)
                .width(350.dp)
                .padding(horizontal = 32.dp, vertical = 1.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Cerrar Sesión", color = White, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Eliminar cuenta",
            color = Color.Black,
            fontSize = 16.sp,
            modifier = Modifier
                .clickable {
                    viewModel.showDeletePopUp()
                }
                .padding(vertical = 8.dp),
            style = androidx.compose.ui.text.TextStyle(
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center
            )
        )
    }

}