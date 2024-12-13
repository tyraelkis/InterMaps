package uji.es.intermaps

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import uji.es.intermaps.View.Route.CreateNewRoute
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
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.UserViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth, viewModel: InterestPlaceViewModel, viewModel1: UserViewModel) {
    
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
                viewModel1
            )
        }
        composable("mainMenu") {
            MainMenu (
                auth,
                navHostController,
                viewModel
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
                auth,
                navHostController,
                viewModel
            )
        }
        composable(
            route = "interestPlaceCreation"
        ) {
            InterestPlaceCreation(navHostController, viewModel)
        }
        composable(
            route = "interestPlaceCreationByToponym"
        ) {
            InterestPlaceCreationByToponym(viewModel)
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
                InterestPlaceSetAlias(navHostController,viewModel, toponym)
            }
        }

        composable(
            route = "createNewRoute"
        ) {
            CreateNewRoute(auth, navHostController, viewModel)
        }
    }
}