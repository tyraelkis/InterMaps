package uji.es.intermaps

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import uji.es.intermaps.Interfaces.ProxyService
import uji.es.intermaps.Model.CachePrecioLuz
import uji.es.intermaps.Model.ConsultorPreciLuz
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.RouteService
import uji.es.intermaps.ViewModel.RouteViewModel
import uji.es.intermaps.ViewModel.UserService
import uji.es.intermaps.ViewModel.UserViewModel
import uji.es.intermaps.ViewModel.VehicleService
import uji.es.intermaps.ViewModel.VehicleViewModel
import uji.es.intermaps.ViewModel.scheduleFuelPriceUpdate
import uji.es.intermaps.ui.theme.InterMapsTheme

class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private lateinit var auth: FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseFirestore.setLoggingEnabled(true)
        scheduleFuelPriceUpdate()
        //scheduleElectricPriceUpdate() Ya no se usa
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            navHostController = rememberNavController()
            val repository = FirebaseRepository()
            val interestPlaceService = InterestPlaceService(repository)
            val userService = UserService(repository)
            val vehicleService = VehicleService(repository)
            val servicioLuz: ProxyService = CachePrecioLuz(ConsultorPreciLuz())
            val routeService = RouteService(repository, servicioLuz)
            InterMapsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        modifier = Modifier.padding(innerPadding)
                    )
                    NavigationWrapper(
                        navHostController,
                        auth,
                        InterestPlaceViewModel(interestPlaceService),
                        UserViewModel(userService, auth),
                        VehicleViewModel(vehicleService),
                        RouteViewModel(routeService = routeService )
                    )

                }
            }
        }
    }
}

@Composable
fun Greeting(modifier: Modifier = Modifier) {
    Text(
        text = "",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    InterMapsTheme {
        Greeting()
    }
}