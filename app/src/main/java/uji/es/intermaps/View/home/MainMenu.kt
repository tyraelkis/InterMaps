package uji.es.intermaps.View.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MainMenu() {

    var busqueda by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .background(White)
        )

        Box( //Rectangulo verde
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0XFF007E70))
        ) {
            Row( //Barra de busqueda
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .padding(vertical = 10.dp)
                    .border(
                        width = 1.dp,
                        color = Color(0xFF92C1C1),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .background(
                        color = Color(0xFF92C1C1),
                        shape = RoundedCornerShape(25.dp)
                    )
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    if (busqueda.isEmpty()) { //Mete texto mientras no se escriba
                        androidx.compose.material3.Text(
                            text = "Coordenadas/Topónimo",
                            color = Color.Black,
                            fontSize = 16.sp,
                            modifier = Modifier.offset(y = (-3).dp)
                        )
                    }
                    BasicTextField(
                        value = busqueda,
                        onValueChange = { busqueda = it },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
