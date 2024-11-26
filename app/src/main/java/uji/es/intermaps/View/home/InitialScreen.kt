package uji.es.intermaps.View.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import uji.es.intermaps.R

@Composable
fun InitialScreen (navigateToLogin: () -> Unit = {}, navigateToSignUp: () -> Unit = {}, navigateToUserDataScreen: () -> Unit = {}){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(painter = painterResource(
            id = R.drawable.logogrande),
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth(0.8f) // Ancho proporcional al 80% de la pantalla
                .aspectRatio(1.2f)
        )
        Spacer(modifier = Modifier.weight(0.5f))
        Text(text = "¿Ya eres usuario de InterMaps?",
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { navigateToLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Iniciar Sesión", color = White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(0.25f))
        Text(text = "¿Eres nuevo?",
            color = Color.DarkGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(
            onClick = { navigateToSignUp() },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = White),
            border = BorderStroke(2.dp, Color.Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Crear Cuenta", color = Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.weight(0.5f))
        Text(text = "Descubre tu futuro lugar favorito...",
            color = Black, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(0.5f))


        /*Button(
            onClick = { navigateToUserDataScreen() },
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Pantalla datos usuario", color = White, fontWeight = FontWeight.Bold)
        }*/
    }
}