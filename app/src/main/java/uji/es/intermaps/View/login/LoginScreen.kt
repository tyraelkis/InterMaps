@file:Suppress("DEPRECATION")

package uji.es.intermaps.View.login

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.textFieldColors
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import uji.es.intermaps.R
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun LoginScreen(auth: FirebaseAuth, navigateToSignUp: () -> Unit = {}, navigateToHome:() -> Unit) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //Icon(painter = painterResource(id = R.drawable.close_24), contentDescription = "")
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "Iniciar Sesión",
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
            text = "Ejemplo: usuario@ejemplo.com",
            color = Color.LightGray,
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

        Spacer(modifier = Modifier.height(64.dp))

        Button(
            onClick = {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener{
                   if(it.isSuccessful){
                       navigateToHome()
                   }
                    else{
                        Log.i("SARA", "NO LOGIN")
                   }
                }
            },
            modifier = Modifier
                .height(42.dp)
                .width(250.dp)
                .padding(horizontal = 32.dp)
                .align(AbsoluteAlignment.Right),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Iniciar Sesión", color = White,fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
            OutlinedButton(
                onClick = { navigateToSignUp() },
                modifier = Modifier
                    .height(36.dp)
                    .width(130.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White),
                border = BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "Crear Cuenta", color = Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

}

