package uji.es.intermaps.View.signup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.NotValidUserData
import uji.es.intermaps.Model.FirebaseRepository
import uji.es.intermaps.Model.Repository
import uji.es.intermaps.Model.UserService

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun SignUpScreen(auth: FirebaseAuth, navigateToLogin: () -> Unit = {}, navigateToHome: () -> Unit) {
    val repository: Repository = FirebaseRepository()
    val userService = UserService(repository)
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    //Variables para mensajes de debajo de los campos
    var submesageEmail by remember { mutableStateOf("Ejemplo: usuario@ejemplo.com") }
    var submesageEmailColor by remember { mutableStateOf(Color.LightGray) }
    var submesagePswd by remember { mutableStateOf("Mínimo 6 carácteres") }
    var submesagePswdColor by remember { mutableStateOf(Color.LightGray) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "Crear Cuenta",
            color = Color.Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Correo Electrónico",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = {email = it},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            placeholder = { Text(
                text = "Ingrese su correo electrónico",
                modifier = Modifier
                    .background(Color.Transparent), // Cambiar el fondo del placeholder
                color = Color.Black // Color del texto del placeholder
            ) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White, // Fondo del TextField
                cursorColor = Color.Black, // Color del cursor
                focusedIndicatorColor = Color.Transparent, // Eliminar el indicador de enfoque
                unfocusedIndicatorColor = Color.Transparent // Eliminar el indicador cuando no está enfocado
            )
        )
        Text(
            text = submesageEmail,
            color = submesageEmailColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "Contraseña",
            color = Color.Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = {password = it},
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp)),
            visualTransformation = PasswordVisualTransformation(),
            placeholder = { Text(
                text = "Ingrese su contraseña",
                modifier = Modifier
                    .background(Color.Transparent), // Cambiar el fondo del placeholder
                color = Color.Black // Color del texto del placeholder
            ) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White, // Fondo del TextField
                cursorColor = Color.Black, // Color del cursor
                focusedIndicatorColor = Color.Transparent, // Eliminar el indicador de enfoque
                unfocusedIndicatorColor = Color.Transparent // Eliminar el indicador cuando no está enfocado
            )
        )
        Text(
            text = submesagePswd,
            color = submesagePswdColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(64.dp))

                OutlinedButton(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        userService.createUser(email, password)
                        withContext(Dispatchers.Main) {
                            navigateToHome()
                        }
                    } catch (e: NotValidUserData) {
                        withContext(Dispatchers.Main) {
                            submesageEmail = e.message.toString()
                            submesageEmailColor = Color.Red
                            submesagePswd = e.message.toString()
                            submesagePswdColor = Color.Red
                        }
                    } catch (e: IllegalArgumentException) {
                        withContext(Dispatchers.Main) {
                            when (e.message.toString()) {
                                "El correo electrónico no tiene un formato válido." -> {
                                    submesageEmail = "El correo electrónico no tiene un formato válido."
                                    submesageEmailColor = Color.Red
                                }
                                "La contraseña debe tener al menos 6 caracteres." -> {
                                    submesagePswd = "La contraseña debe tener al menos 6 caracteres."
                                    submesagePswdColor = Color.Red
                                }
                                "La contraseña no tiene un formato válido." -> {
                                    submesagePswd = "La contraseña no tiene un formato válido."
                                    submesagePswdColor = Color.Red
                                }
                            }
                        }
                    } catch (e: AccountAlreadyRegistredException) {
                        withContext(Dispatchers.Main) {
                            submesageEmail = e.message.toString()
                            submesageEmailColor = Color.Red
                            submesagePswd = e.message.toString()
                            submesagePswdColor = Color.Red
                        }
                    }
                }
            },
            modifier = Modifier
                .height(42.dp)
                .width(250.dp)
                .padding(horizontal = 32.dp)
                .align(AbsoluteAlignment.Right),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            border = BorderStroke(2.dp, Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Crear Cuenta", color = Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(64.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Si ya tienes una cuenta",
                color = Color.Black,
            )
            Button(
                onClick = { navigateToLogin() },
                modifier = Modifier
                    .height(36.dp)
                    .width(140.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Iniciar Sesión", color = White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}