package uji.es.intermaps.View.user

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import uji.es.intermaps.R
import uji.es.intermaps.View.CustomDropdownMenu
import uji.es.intermaps.ViewModel.UserViewModel

@SuppressLint("CoroutineCreationDuringComposition")
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

    val user = auth.currentUser
    val currentEmail by remember { mutableStateOf(user?.email.toString()) }

    DeleteUserPopUp(viewModel, navController)
    ModificationUserPopUp(viewModel)
    PasswordUserPopUp(viewModel, navController)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(White),
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
                color = Black,
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

        CustomDropdownMenu(
            label = "Vehículo pred.",
            options = optionsVehicles,
            selectedOption = selectedOptionVehicles,
            onOptionSelected = { selectedOptionVehicles = it },
            expanded = expandedVehicles,
            onExpandedChange = { expandedVehicles = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdownMenu(
            label = "Tipo de ruta pred.",
            options = optionsRoutes,
            selectedOption = selectedOptionRoutes,
            onOptionSelected = { selectedOptionRoutes = it },
            expanded = expandedRoutes,
            onExpandedChange = { expandedRoutes = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdownMenu(
            label = "Tipo de transporte pred.",
            options = optionsTransport,
            selectedOption = selectedOptionTransport,
            onOptionSelected = { selectedOptionTransport = it },
            expanded = expandedTransport,
            onExpandedChange = { expandedTransport = it }
        )
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
                viewModel.signOut(navController)
            },
            modifier = Modifier
                .height(45.dp)
                .width(350.dp)
                .padding(horizontal = 32.dp, vertical = 1.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Cerrar Sesión", color = White, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Eliminar cuenta",
            color = Black,
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