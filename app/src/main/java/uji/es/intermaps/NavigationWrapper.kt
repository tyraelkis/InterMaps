package uji.es.intermaps

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import uji.es.intermaps.View.home.HomeScreen
import uji.es.intermaps.View.home.InitialScreen
import uji.es.intermaps.View.home.MainMenu
import uji.es.intermaps.View.interestPlace.InterestPlaceCreation
import uji.es.intermaps.View.interestPlace.InterestPlaceCreationByToponym
import uji.es.intermaps.View.interestPlace.InterestPlaceList
import uji.es.intermaps.View.interestPlace.InterestPlaceSetAlias
import uji.es.intermaps.View.login.LoginScreen
import uji.es.intermaps.View.signup.SignUpScreen
import uji.es.intermaps.View.user.UserDataScreen
import uji.es.intermaps.View.vehicle.VehicleCreate
import uji.es.intermaps.View.vehicle.VehicleList
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.UserViewModel
import uji.es.intermaps.ViewModel.VehicleViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth, viewModelPlace: InterestPlaceViewModel, viewModelUser: UserViewModel, viewModelVehicle: VehicleViewModel) {
    
    NavHost(
        navController = navHostController,
        startDestination = "initial",
        ) {
        composable("initial") {
            InitialScreen(
                navHostController
            )
        }
        composable("logIn") {
            LoginScreen(
                navHostController
            )
        }
        composable("signUp") {
            SignUpScreen(
                navHostController
            )
        }
        composable("home") {
            HomeScreen (
                navHostController,
                viewModelUser
            )
        }
        composable("mainMenu") {
            MainMenu (
                auth,
                navHostController,
                viewModelPlace
            )
        }
        composable("userDataScreen") {
            UserDataScreen(
                auth,
                navHostController,
                viewModelUser
            )
        }
        composable("interestPlaceList") {
            InterestPlaceList(
                auth,
                navHostController,
                viewModelPlace
            )
        }
        composable(
            route = "interestPlaceCreation"
        ) {
            InterestPlaceCreation(navHostController, viewModelPlace)
        }
        composable(
            route = "interestPlaceCreationByToponym"
        ) {
            InterestPlaceCreationByToponym(viewModelPlace)
        }
        composable(
            route = "interestPlaceSetAlias/{toponym}",
            arguments = listOf(
                navArgument("toponym") { type = NavType.StringType}
            )
        ) {
            backStackEntry ->
            val toponym = backStackEntry.arguments?.getString("toponym")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            }
            if (toponym != null) {
                InterestPlaceSetAlias(navHostController,viewModelPlace, toponym)
            }
        }

        composable("vehicleList") {
            VehicleList(
                auth,
                navHostController,
                viewModelVehicle
            )
        }

        composable(
            route = "vehicleCreate"
        ) {
            VehicleCreate(navHostController, viewModelVehicle)
        }

        /*
        composable(
            route = "vehicleEdit/{plate}",
            arguments = listOf(
                navArgument("plate") { type = NavType.StringType}
            )
        ) {
                backStackEntry ->
            val plate = backStackEntry.arguments?.getString("plate")?.let {
                URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
            }
            if (plate != null) {
                VehicleEdit(navHostController, viewModelPlace, plate)
            }
        }*/
    }
}