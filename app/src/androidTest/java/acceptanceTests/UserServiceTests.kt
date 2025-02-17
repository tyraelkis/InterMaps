package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.IncorrectDataException
import uji.es.intermaps.Exceptions.NotSuchTransportException
import uji.es.intermaps.Exceptions.NotSuchVehicleException
import uji.es.intermaps.Exceptions.NotValidParameterException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnableToDeleteUserException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.User
import uji.es.intermaps.ViewModel.UserService

@RunWith(AndroidJUnit4::class)
class UserServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var email: String = "prueba@uji.es"
    private var password: String = "123456AA"
    private var userService: UserService = UserService(repository)
    private var userTest: User = User("emaildeprueba@gmail.com", "123456BB")


    @Test
    fun createUser_E1Valid_userIsCreated(): Unit = runBlocking {
        userService.createUser(email, password)
        val res = db.doesUserExist(email)
        userService.deleteUser(email, password)
        assertEquals(true, res)

    }

    @Test(expected = AccountAlreadyRegistredException::class)
    fun createUser_E2Invalid_errorOnAccountCreation(): Unit = runBlocking {
        userService.createUser(userTest.email, userTest.pswd)
    }

    @Test
    fun login_E1Valid_userIsLogged(): Unit = runBlocking {
        val res = userService.login(userTest.email, userTest.pswd)
        assertEquals(true, res)
    }

    @Test(expected = UnregistredUserException::class)
    fun login_E2Invalid_errorOnLogin(): Unit = runBlocking {
        userService.login(email, password)
    }

    @Test
    fun viewUserData_E1Valid_userDataViewed(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        val userData = userService.viewUserData(userTest.email)
        userService.signOut()
        assertEquals(true, userData)
    }

    @Test(expected = SessionNotStartedException::class)
    fun viewUserData_E2Invalid_userDataNotViewed(): Unit = runBlocking {
        val emailError = "noexiste@gmail.com"
        userService.viewUserData(emailError)
    }

    @Test
    fun editUserData_E1Valid_userDataEdited(): Unit = runBlocking {
        userService.createUser(email, password)
        val newPassword = "GuillemElMejor"
        val res = userService.editUserData(newPassword)
        userService.deleteUser(email, newPassword)
        assertEquals(true, res)
    }

    @Test(expected = IncorrectDataException::class)
    fun editUserData_E2Invalid_userDataEdited(): Unit = runBlocking {
        val newPassword = "J"
        userService.editUserData(newPassword)
    }


    @Test
    fun signOut_E1Valid_userSignedOut(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        val res = userService.signOut()
        assertEquals(true, res)
    }

    @Test(expected = SessionNotStartedException::class)
    fun signOut_E2Invalid_errorSigningOut(): Unit = runBlocking {
        userService.signOut()
    }

    @Test
    fun deleteUser_E1Valid_userIsDeleted(): Unit = runBlocking {
        userService.createUser(email, password)
        assertEquals(true, userService.deleteUser(email, password))
    }

    @Test(expected = UnableToDeleteUserException::class)
    fun deleteUser_E2Invalid_userIsDeleted(): Unit = runBlocking {
        userService.deleteUser(email, password)
    }

    @Test
    fun createPrefferedVehicle_E1Valid_PreferredVehicleCreated(): Unit = runBlocking{
        userService.login(userTest.email, userTest.pswd)
        userService.updateUserVehicle("1111AAA")
        val res = db.doesPreferredAttributeExist("preferredVehicle", "1111AAA")
        userService.signOut()
        assertEquals(true, res)
    }

    @Test (expected = NotSuchVehicleException::class)
    fun createPrefferedVehicle_E2Invalid_PreferredVehicleNotCreated(): Unit = runBlocking{
        userService.updateUserVehicle("")
    }

    @Test
    fun createPrefferedTransport_E1Valid_PreferredTransportCreated(): Unit = runBlocking{
        userService.login(userTest.email, userTest.pswd)
        userService.updateUserTransportMethod("BICICLETA")
        val res = db.doesPreferredAttributeExist("preferredTransportMethod", "BICICLETA")
        userService.signOut()
        assertEquals(true, res)
    }

    @Test (expected = NotSuchTransportException::class)
    fun createPrefferedTransport_E2Invalid_PreferredTransportNotCreated(): Unit = runBlocking{
        userService.updateUserTransportMethod("")
    }

    @Test
    fun eliminatePrefferedVehicle_E1Valid_PreferredVehicleEliminated(): Unit = runBlocking{
        userService.login(userTest.email, userTest.pswd)
        userService.updateUserVehicle("Ninguno")
        val res = db.doesPreferredAttributeExist("preferredVehicle", "Ninguno")
        userService.signOut()
        assertEquals(true, res)
    }

    @Test
    fun eliminatePrefferedTransport_E1Valid_PreferredTransportEliminated(): Unit = runBlocking{
        userService.login(userTest.email, userTest.pswd)
        userService.updateUserTransportMethod("Ninguno")
        val res = db.doesPreferredAttributeExist("preferredTransportMethod", "Ninguno")
        userService.signOut()
        assertEquals(true, res)
    }

    @Test
    fun seePrefferedVehicle_E1Valid_PreferredVehicleViewed(): Unit = runBlocking{
        userService.login(userTest.email, userTest.pswd)
        val res = userService.getUserVehicle().first
        userService.signOut()
        assertEquals(true, res)
    }

    @Test
    fun seePrefferedTransport_E1Valid_PreferredTransportViewed(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        val res = userService.getUserTransportMethod().first
        userService.signOut()
        assertEquals(true, res)
    }
    @Test
    fun createPrefferedRouteType_E1Valid_PreferredRouteTypeCreated(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        userService.updateUserRouteType(RouteTypes.RAPIDA.toString())
        val res = db.doesPreferredAttributeExist("preferredRouteType", RouteTypes.RAPIDA.toString())
        userService.signOut()
        assertEquals(true, res)
    }

    @Test (expected = NotValidParameterException::class)
    fun createPrefferedRouteType_E3Invalid_PreferredVehicleNotCreated(): Unit = runBlocking{
        userService.updateUserRouteType("bicicleta")
    }

    @Test
    fun eliminatePrefferedRouteType_E1Valid_PreferredRouteTypeEliminated(): Unit = runBlocking{
        userService.login(userTest.email, userTest.pswd)
        userService.updateUserRouteType("Ninguno")
        val res = db.doesPreferredAttributeExist("preferredRouteType", "Ninguno")
        userService.signOut()
        assertEquals(true, res)
    }



    @Test
    fun seePrefferedRouteType_E1Valid_PreferredRouteTypeViewed(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        val res = userService.getUserRouteType().first
        userService.signOut()
        assertEquals(true, res)

    }
}