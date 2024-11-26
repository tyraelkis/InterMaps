package uji.es.intermaps.View.user

import android.util.Log
import android.util.Log.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import uji.es.intermaps.R
import java.time.format.TextStyle
import kotlin.math.log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserDataScreen(auth: FirebaseAuth, navigateToUserChangeEmail: () -> Unit = {}, navigateToUserChangePassword: () -> Unit){

    var expandedVehicles by remember { mutableStateOf(false) }
    var expandedRoutes by remember { mutableStateOf(false) }
    var expandedTransport by remember { mutableStateOf(false) }
    val optionsVehicles = listOf("...", "Gasolina", "Diesel", "Eléctrico")
    val optionsRoutes = listOf("...", "Rápida", "Corta", "Económica")
    val optionsTransport = listOf("...", "En coche", "A pie", "En bicicleta")
    var selectedOptionVehicles by remember { mutableStateOf(optionsVehicles[0]) }
    var selectedOptionRoutes by remember { mutableStateOf(optionsRoutes[0]) }
    var selectedOptionTransport by remember { mutableStateOf(optionsRoutes[0]) }

    var showPopupEmail by remember { mutableStateOf(false) }
    var showPopupPassword by remember { mutableStateOf(false) }
    var showPopupModifications by remember { mutableStateOf(false) }
    var newEmail by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var user = auth.currentUser




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
                text = "Nombre de usuario",
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(50.dp))

        Box(
            modifier = Modifier
                .height(45.dp)
                .width(350.dp)
                .background(Color(0XFF808080), shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            // Texto en el fondo
            Text(
                text = user?.email ?: "Correo electronico",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {showPopupEmail = true},
            modifier = Modifier
                .height(36.dp)
                .padding(horizontal = 32.dp, vertical = 1.dp)
                .align(AbsoluteAlignment.Right),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Editar", color = White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .height(45.dp)
                .width(350.dp)
                .background(Color(0XFF808080), shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            // Texto en el fondo
            Text(
                text = "Contraseña Oculta",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {showPopupPassword = true},
            modifier = Modifier
                .height(36.dp)
                .padding(horizontal = 32.dp, vertical = 1.dp)
                .align(AbsoluteAlignment.Right),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Cambiar Contraseña", color = White, fontSize = 14.sp)
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
                        lineHeight = 20.sp,  // Ajustar el lineHeight para evitar corte, manteniendo el texto centrado
                        textAlign = TextAlign.Center  // Asegura que el texto esté centrado verticalmente
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
                        lineHeight = 20.sp,  // Ajustar el lineHeight para evitar corte, manteniendo el texto centrado
                        textAlign = TextAlign.Center  // Asegura que el texto esté centrado verticalmente
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
        Spacer(modifier = Modifier.height(64.dp))

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
            onClick = {},
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

                }
                .padding(vertical = 8.dp),
            style = androidx.compose.ui.text.TextStyle(
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center
            )
        )

    }
    if (showPopupEmail) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .background(Color(0x80FFFFFF))
                .clickable { showPopupEmail = false},
            contentAlignment = Alignment.Center
        ) {
            // Popup con el contenido de edición de correo
            Box(
                modifier = Modifier
                    .height(450.dp)
                    .width(395.dp)
                    .background(Color(0XFF007E70), shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cambio de correo electrónico",
                        color = Color.White,
                        fontSize = 26.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    // Nuevo correo electrónico
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Nuevo correo electrónico",
                            color = Color.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            modifier = Modifier
                                .height(52.dp)
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                            placeholder = { Text("Introduce el nuevo correo electrónico") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFF80BEB7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))

                    // Confirmación de correo electrónico
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Confirmación de correo electrónico",
                            color = Color.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Left,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = newEmail,
                            onValueChange = { newEmail = it },
                            modifier = Modifier
                                .height(52.dp)
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                            placeholder = { Text("Confirma el nuevo correo electrónico") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFF80BEB7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(50.dp))

                    // Botones de aceptar y cancelar
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                showPopupEmail = false
                                showPopupModifications = true
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Aceptar",
                                fontSize = 16.sp,
                            )
                        }

                        Button(
                            onClick = {
                                showPopupEmail = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancelar",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPopupPassword) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .background(Color(0x80FFFFFF))
                .clickable { showPopupPassword = false },
            contentAlignment = Alignment.Center
        ) {
            // Popup con el contenido de edición de correo
            Box(
                modifier = Modifier
                    .height(450.dp)
                    .width(395.dp)
                    .background(Color(0XFF007E70), shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
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
                            onValueChange = { newPassword = it },
                            modifier = Modifier
                                .height(52.dp)
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(10.dp)),
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
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            modifier = Modifier
                                .height(52.dp)
                                .fillMaxWidth()
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
                            placeholder = { Text("Confirma la nueva contraseña") },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFF80BEB7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(50.dp))


                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                showPopupPassword = false
                                showPopupModifications = true
                             },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Aceptar",
                                fontSize = 16.sp,
                            )
                        }

                        Button(
                            onClick = { showPopupPassword = false },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancelar",
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

    }

    if (showPopupModifications) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .background(Color(0x80FFFFFF))
                .clickable { showPopupPassword = false },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(350.dp)
                    .background(Color(0XFF007E70), shape = RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Se han modificado los datos correctamente",
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
                        onClick = { showPopupModifications = false },
                        modifier = Modifier.fillMaxWidth(),
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
                }
            }
        }
    }

}