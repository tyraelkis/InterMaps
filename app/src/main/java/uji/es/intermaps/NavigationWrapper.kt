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
import uji.es.intermaps.View.interestPlace.InterestPlaceList
import uji.es.intermaps.View.interestPlace.InterestPlaceSetAlias
import uji.es.intermaps.View.login.LoginScreen
import uji.es.intermaps.View.signup.SignUpScreen
import uji.es.intermaps.View.user.UserDataScreen
import uji.es.intermaps.ViewModel.InterestPlaceViewModel
import uji.es.intermaps.ViewModel.UserViewModel

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth, viewModel: InterestPlaceViewModel, viewModel1: UserViewModel) {

    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route

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
                navigateToInterestPlaceSetAlias = { navHostController.navigate("interestPlaceSetAlias") },
                navigateToInterestPlaceCreation = { navHostController.navigate("interestPlaceCreation") },
                viewModel
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
    /*NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial"){
            InitialScreen(
                navigateToLogin = {navHostController.navigate("logIn")},
                navigateToSignUp = {navHostController.navigate("signUp")}
            )
        }
        composable("logIn"){
            LoginScreen(
                auth,
                navigateToSignUp = {navHostController.navigate("signUp")},
                navigateToHome = {navHostController.navigate("home")}
            )
        }
        composable("signUp") {
            SignUpScreen(
                auth,
                navigateToLogin = {navHostController.navigate("logIn")},
                navigateToHome = {navHostController.navigate("home")}

            )
        }
        composable("home"){
            HomeSreen(
                navigateToUserDataScreen = {navHostController.navigate("userDataScreen")},
                navigateToInterestPlaceList = {navHostController.navigate("interestPlaceList")}
                )
        }
        composable("mainMenu") {
            MainMenu (
            )
        }
        composable("userDataScreen"){
            UserDataScreen(
                auth
            )
        }

        composable("interestPlaceList"){
            InterestPlaceList(navigateToInterestPlaceList = {navHostController.navigate("interestPlaceList")},
                auth,
                navigateToInterestPlaceSetAlias = {navHostController.navigate("interestPlaceSetAlias")},
                navigateToInterestPlaceCreation = {navHostController.navigate("interestPlaceCreation")},
                viewModel
            )
        }

        composable("interestPlaceCreation"){
            InterestPlaceCreation(
                viewModel
            )
        }

        composable("interestPlaceSetAlias"){
            InterestPlaceSetAlias(
                viewModel
            )
        }

        composable("mapaScreen"){
            MapaScreen()
        }

    }*/




