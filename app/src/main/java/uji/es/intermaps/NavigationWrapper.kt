package uji.es.intermaps

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import uji.es.intermaps.View.home.HomeScreen
import uji.es.intermaps.View.home.InitialScreen
import uji.es.intermaps.View.interestPlace.InterestPlaceCreation
import uji.es.intermaps.View.interestPlace.InterestPlaceList
import uji.es.intermaps.View.interestPlace.InterestPlaceSetAlias
import uji.es.intermaps.View.login.LoginScreen
import uji.es.intermaps.View.signup.SignUpScreen
import uji.es.intermaps.View.navBar.NavBar
import uji.es.intermaps.View.user.UserDataScreen
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.UserViewModel

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth, viewModel: InterestPlaceViewModel, viewModel1: UserViewModel) {

    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route

    Scaffold(
        bottomBar = {
            // Mostrar la barra de navegación solo en ciertas pantallas
            if (currentRoute !in listOf("logIn", "signUp", "initial")) {
                NavBar(
                    currentRoute = currentRoute ?: "initial",
                    onNavigate = { route ->
                        if (route != currentRoute) { // Evitar recargar la misma ruta
                            navHostController.navigate(route) {
                                if (route == "home"){
                                    popUpTo(navHostController.graph.startDestinationId){
                                        inclusive = true
                                    }
                                }
                                else{
                                    popUpTo(navHostController.graph.startDestinationId){
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navHostController,
            startDestination = "initial",
            modifier = Modifier.padding(padding)
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
                    viewModel1
                )
            }
            composable("userDataScreen") {
                UserDataScreen(
                    auth,
                    navHostController,
                    viewModel1
                    )
            }
            composable("interestPlaceList") {
                InterestPlaceList(
                    navHostController,
                    auth
                )
            }
            composable(
                route = "interestPlaceCreation"

            ) {
                InterestPlaceCreation(viewModel)
            }
            composable(
                route = "interestPlaceSetAlias/{toponym}",
                arguments = listOf(
                    navArgument("toponym") { type = NavType.StringType}
                )
            ) {
                    backStackEntry ->
                val toponym = backStackEntry.arguments?.getString("toponym")
                if (toponym != null) {
                    InterestPlaceSetAlias(viewModel, toponym)
                }

            }


        }
    }
}





