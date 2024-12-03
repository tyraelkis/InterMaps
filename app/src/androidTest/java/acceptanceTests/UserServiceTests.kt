package acceptanceTests

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.IncorrectDataException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnableToDeleteUserException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.User
import uji.es.intermaps.ViewModel.UserService

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var email: String = "prueba@uji.es"
    private var password: String = "123456AA" // Cambiar en las pruebas de aceptacion para que cumpla los requisitos de las contraseñas
    private var userService: UserService = UserService(repository)
    private var userTest: User = User("emaildeprueba@gmail.com", "123456BB")


    @Test
    fun createUser_E1Valid_userIsCreated(): Unit = runBlocking {
        userService.createUser(email, password)
        val res = db.doesUserExist(email)
        userService.deleteUser(email, password)
        assertEquals(true, res)

    }

    @Test
    fun createUser_E2Invalid_errorOnAccountCreation(): Unit = runBlocking {
        userService.createUser(email, password)
        assertThrows<AccountAlreadyRegistredException> {
            userService.createUser(email, password)
        }
        userService.deleteUser(email, password)
    }

    @Test
    fun login_E1Valid_userIsLogged(): Unit = runBlocking {
        val res = userService.login(userTest.email, userTest.pswd)
        assertEquals(true, res)
    }

    @Test
    fun login_E2Invalid_errorOnLogin(): Unit = runBlocking {
        assertThrows<UnregistredUserException> {
            userService.login(email, password)
        }
    }

    @Test
    fun viewUserData_E1Valid_userDataViewed(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        val userData = userService.viewUserData(userTest.email)
        userService.signOut()
        assertEquals(true, userData)
    }

    @Test
    fun viewUserData_E1Invalid_userDataNotViewed(): Unit = runBlocking {
        val emailError = "noexiste@gmail.com"
        assertThrows<SessionNotStartedException> {
            userService.viewUserData(emailError)
        }
    }

    @Test
    fun editUserData_E1Valid_userDataEdited(): Unit = runBlocking {
        userService.createUser(email, password)
        val newPassword = "GuillemElMejor"
        val res = userService.editUserData(newPassword)
        userService.deleteUser(email, newPassword)
        assertEquals(true, res)
    }

    @Test
    fun editUserData_E1Invalid_userDataEdited(): Unit = runBlocking {
        val newPassword = "J"
        assertThrows<IncorrectDataException> {
            userService.editUserData(newPassword)
        }
    }


    @Test
    fun signOut_E1Valid_userSignedOut(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        val res = userService.signOut()
        assertEquals(true, res)
    }

    @Test
    fun signOut_E2Invalid_errorSigningOut(): Unit = runBlocking {
        assertThrows<SessionNotStartedException> {
            userService.signOut()
        }
    }

    @Test
    fun deleteUser_E1Valid_userIsDeleted(): Unit = runBlocking {
        userService.createUser(email, password)
        assertEquals(true, userService.deleteUser(email, password))
    }

    @Test
    fun deleteUser_E1Invalid_userIsDeleted(): Unit = runBlocking {
        assertThrows<UnableToDeleteUserException> {
            userService.deleteUser(email, password)
        }
    }
}