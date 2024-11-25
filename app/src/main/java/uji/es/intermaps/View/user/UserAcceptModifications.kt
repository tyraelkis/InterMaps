package uji.es.intermaps.View.user

import android.util.Log
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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import uji.es.intermaps.R
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAcceptModifications(navigateToUserDataScreen: () -> Unit = {}){

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
                .clip(RoundedCornerShape(15.dp))
                .padding(5.dp),
            contentAlignment = Alignment.Center
        ) {
            // Texto en el fondo
            Text(
                text = "Correo Electrónico",
                color = Color.White,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {navigateToUserDataScreen()},
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
            onClick = {navigateToUserDataScreen()},
            modifier = Modifier
                .height(36.dp)
                .padding(horizontal = 32.dp, vertical = 1.dp)
                .align(AbsoluteAlignment.Right),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Cambiar Contraseña", color = White, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .height(200.dp)
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
                    text = "Se han modificado los datos correctamente",
                    color = Color.White,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    style = androidx.compose.ui.text.TextStyle(
                        lineHeight = 32.sp // Ajusta la separación entre líneas
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { navigateToUserDataScreen() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Aceptar",
                        fontSize = 16.sp,
                    )
                }
            }

            }
        Spacer(modifier = Modifier.height(32.dp))



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


}