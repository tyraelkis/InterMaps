package uji.es.intermaps

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import uji.es.intermaps.View.home.HomeSreen
import uji.es.intermaps.View.home.InitialScreen
import uji.es.intermaps.View.interestPlace.InterestPlaceList
import uji.es.intermaps.View.interestPlace.InterestPlaceSetAlias
import uji.es.intermaps.View.login.LoginScreen
import uji.es.intermaps.View.signup.SignUpScreen
import uji.es.intermaps.View.user.UserAcceptModifications
import uji.es.intermaps.View.user.UserDataScreen

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth){
    NavHost(navController = navHostController, startDestination = "interestPlaceList") {
        composable("initial"){
            InitialScreen(
                navigateToLogin = {navHostController.navigate("logIn")},
                navigateToSignUp = {navHostController.navigate("signUp")},
                navigateToUserDataScreen = {navHostController.navigate("userDataScreen")}

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
            HomeSreen()
        }
        composable("userDataScreen"){
            UserDataScreen(
                auth,
                navigateToUserChangeEmail = {navHostController.navigate("userChangeEmail")},
                navigateToUserChangePassword = {navHostController.navigate("userChangePassword")}

            )
        }
        composable("userAcceptModifications"){
            UserAcceptModifications(
                navigateToUserDataScreen = {navHostController.navigate("userDataScreen")}
            )
        }

        composable("interestPlaceList"){
            InterestPlaceList(navigateToInterestPlaceList = {navHostController.navigate("interestPlaceList")}, auth)
        }

        composable("interestPlaceSetAlias"){
            InterestPlaceSetAlias(
                navigateToInterestPlaceSetAlias = {navHostController.navigate("interestPlaceSetAlias")}
            )
        }
    }


}

