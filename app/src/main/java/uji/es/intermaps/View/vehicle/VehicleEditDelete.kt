package uji.es.intermaps.View.vehicle

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import uji.es.intermaps.Model.VehicleTypes
import uji.es.intermaps.ViewModel.VehicleViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleEditDelete(navController: NavController, viewModel: VehicleViewModel, plate: String) {
    var showPopupEditSucces by remember { mutableStateOf(false) }
    var showPopupDeleteConfirm by remember { mutableStateOf(false) }

    var vehicle by remember { mutableStateOf(viewModel.getVehicle()) }
    var consumption by remember { mutableStateOf(vehicle.consumption.toString()) }
    LaunchedEffect(plate) {
        vehicle = viewModel.getVehicleData(plate)
    }

    var expanded by remember { mutableStateOf(false) }
    val vehicleOptions = listOf(VehicleTypes.GASOLINA.type, VehicleTypes.DIESEL.type, VehicleTypes.ELECTRICO.type)

    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                White
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Modificar Vehículo\n${vehicle.plate}",
            color = Black,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(50.dp))

        //Campos del tipo
        Text(
            text = "Tipo",
            color = Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .border(1.dp, LightGray, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = vehicle.type,
                    color = Black
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Dropdown Arrow"
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                vehicleOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            vehicle.type = option
                            expanded = false
                        }
                    )
                }
            }
        }

        Text(
            text = "Seleccione uno de los tipos",
            color = LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(25.dp))

        //Campos del consumo
        Text(
            text = "Consumo medio",
            color = Black,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = consumption,
            onValueChange = {
                consumption = it
                vehicle.consumption = it.toDoubleOrNull() ?: 0.0
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .border(1.dp, LightGray, RoundedCornerShape(8.dp)),
            placeholder = { Text(
                text = "Ingrese el consumo de su vehículo",
                modifier = Modifier
                    .background(Color.Transparent),
                color = LightGray
            ) },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = White,
                cursorColor = Black,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
        Text(
            text = "Gasolina y diesel en l, eléctrico en kWh",
            color = LightGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = errorMessage,
            color = Red,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            textAlign = TextAlign.Left
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.editVehicleData(vehicle.plate, vehicle.type, vehicle.consumption)
                    CoroutineScope(Dispatchers.Main).launch {
                        errorMessage = viewModel.getErrorMessage()
                        if (errorMessage.isEmpty())
                            showPopupEditSucces = viewModel.getShowPopupEditSucces()
                    }
                }
            },
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Modificar datos", color = White,fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                showPopupDeleteConfirm = true
            },
            modifier = Modifier
                .height(40.dp)
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Black),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Eliminar vehículo", color = White,fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    if (showPopupDeleteConfirm) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 350.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0XFF007E70))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "¿Estas seguro de eliminar el vehículo ${plate}?",
                    color = White,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(40.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Button(
                        onClick = {
                            showPopupDeleteConfirm = false
                        },
                        modifier = Modifier.weight(1f)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "No", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                viewModel.deleteVehicle(plate)
                                CoroutineScope(Dispatchers.Main).launch {
                                    errorMessage = viewModel.getErrorMessage()
                                    if (errorMessage.isEmpty())
                                        navController.navigate("vehicleList")
                                }
                            }
                        },
                        modifier = Modifier.weight(1f)
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Sí", fontSize = 16.sp)
                    }
                }
            }
        }
    }

    if (showPopupEditSucces) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 350.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0XFF007E70))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Se ha modificado correctamente el vehículo ${plate}",
                    color = White,
                    fontSize = 26.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        navController.navigate("vehicleList")
                        },
                    modifier = Modifier.weight(1f)
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Black),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(text = "Aceptar", fontSize = 16.sp)
                }
            }
        }
    }
}