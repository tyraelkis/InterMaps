package uji.es.intermaps

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth,viewModel: InterestPlaceViewModel){
    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial"){
            InitialScreen(
                navigateToLogin = {navHostController.navigate("logIn")},
                navigateToSignUp = {navHostController.navigate("signUp")}
            )
        }
        composable("logIn"){
            LoginScreen(
                navigateToSignUp = {navHostController.navigate("signUp")},
                navigateToHome = {navHostController.navigate("home")}
            )
        }
        composable("signUp") {
            SignUpScreen(
                navigateToLogin = {navHostController.navigate("logIn")},
                navigateToHome = {navHostController.navigate("home")}

            )
        }
        composable("home"){
            HomeScreen(
                navigateToUserDataScreen = {navHostController.navigate("userDataScreen")},
                navigateToInterestPlaceList = {navHostController.navigate("interestPlaceList")}
                )
        }

        composable("mainMenu"){
            MainMenu(
                //TODO Poner todos los enlaces
            )
        }

        composable("userDataScreen"){
            UserDataScreen(
                auth,
                navigateToInitialScreen = {navHostController.navigate("initial")}
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

    }


}

