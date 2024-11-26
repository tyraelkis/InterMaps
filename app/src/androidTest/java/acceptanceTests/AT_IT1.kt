package acceptanceTests


import uji.es.intermaps.Exceptions.NotValidAliasException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import org.junit.Test
import uji.es.intermaps.Model.User
import org.junit.Assert.*
import org.junit.jupiter.api.assertThrows
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.IncorrectDataException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnableToDeleteUserException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.InterestPlaceService
import uji.es.intermaps.Model.Repository
import uji.es.intermaps.Model.UserService

class AT_IT1 {
    private var auth: FirebaseAuth = Firebase.auth
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var email: String = "prueba@uji.es"
    private var password: String = "12345"
    private var user: User = User(email, password)
    private var userService: UserService = UserService(repository)
    private var interestPlace: InterestPlace = InterestPlace(GeoPoint(-18.665695, 35.529562), "Mozambique", "Moz", false)
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)

    @Test
    fun createUser_E1Valid_userIsCreated() {
        val userTest: User = userService.createUser(email, password)
        assertEquals(user,userTest)
        userService.deleteUser(userTest.email)
    }

    @Test
    fun createUser_E2Invalid_errorOnAccountCreation() {
        assertThrows<AccountAlreadyRegistredException> {
            userService.createUser(email, password)
        }
    }

    @Test
    fun login_E1Valid_userIsLogged() {
        val userTest: User = userService.createUser(email, password)
        assertEquals(true, userService.login(userTest.email,userTest.password))
        userService.deleteUser(userTest.email)
    }

    @Test
    fun login_E2Invalid_errorOnLogin() {
        assertThrows<UnregistredUserException>{
            userService.login(email, password)
        }
    }

    @Test
    fun viewUserData_E1Valid_userDataViewed() {
        val userData: User? = userService.viewUserData(email)
        assertEquals(user, userData)
    }

    @Test
    fun viewUserData_E1Invalid_userDataNotViewed() {
        assertThrows<SessionNotStartedException> {
            userService.viewUserData(email)
        }
    }

    @Test
    fun editUserData_E1Valid_userDataEdited() {
        val newPassword = "GuillemElMejor"
        userService.editUserData(email, newPassword)
        assertEquals(true, newPassword)
    }

    @Test
    fun editUserData_E1Invalid_userDataEdited(){
        val newPassword = "J"
        assertThrows<IncorrectDataException>{
            userService.editUserData(email, newPassword)
        }
    }


    @Test
    fun signOut_E1Valid_userSignedOut() {
        val userTest: User = userService.createUser(email, password)
        userService.login(userTest.email, userTest.password)
        assertEquals(true, userService.signOut(userTest.email,userTest.password))
        userService.deleteUser(userTest.email)
    }

    @Test
    fun signOut_E2Invalid_errorSigningOut() {
        val userTest: User = userService.createUser(email, password)
        assertThrows<SessionNotStartedException>{
            userService.signOut(userTest.email,userTest.password)
        }
        userService.deleteUser(userTest.email)
    }

    @Test
    fun deleteUser_E1Valid_userIsDeleted() {
        val initialCount = db.getNumberUsers()
        userService.deleteUser(email)
        val finalCount = db.getNumberUsers()
        assertEquals(initialCount - 1, finalCount)
    }

    @Test
    fun deleteUser_E1Invalid_userIsDeleted() {
        assertThrows<UnableToDeleteUserException>{
            userService.deleteUser(email)
        }
    }

    @Test
    fun createInterestPlace_E1Valid_InterestPlaceCreated() {
        val interestPlaceTest: InterestPlace = interestPlaceService.createInterestPlace(GeoPoint(-18.665695, 35.529562), "Mozambique", "Moz")
        assertEquals(interestPlace, interestPlaceTest)
        interestPlaceService.deleteInterestPlace(interestPlaceTest.coordinate)
    }

    @Test
    fun createInterestPlace_E2Invalid_errorOnCreatingInterestPlace() {
        assertThrows<NotValidCoordinatesException>{
            interestPlaceService.createInterestPlace(GeoPoint(-1800.665695,35.529562), "Mozambique", "Moz")
        }
    }

    @Test
    fun editInterestPlace_E1Valido_setAliasToAPlaceOfInterest() {
        val result: Boolean = interestPlaceService.setAlias(interestPlace, newAlias = "Mozambiquinho")
        assertEquals(true, result)
    }

    @Test
    fun editInterestPlace_E1Invalido_errorSetAliasToAPlaceOFInterest(){
        assertThrows<NotValidAliasException>{
            interestPlaceService.setAlias(interestPlace, newAlias = "@#//")
        }
    }

}