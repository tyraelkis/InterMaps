package acceptanceTests

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.IncorrectDataException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnableToDeleteUserException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.FirebaseRepository
import uji.es.intermaps.Model.Repository
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.UserService

class UserServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var email: String = "prueba@uji.es"
    private var password: String = "123456AA" // Cambiar en las pruebas de aceptacion para que cumpla los requisitos de las contraseñas
    private var user: User = User(email, password)
    private var userService: UserService = UserService(repository)

    @Test
    fun createUser_E1Valid_userIsCreated(): Unit = runBlocking {
        val userTest: User = userService.createUser(email, password)
        assertEquals(true, db.doesUserExist(userTest.email))
        userService.deleteUser(userTest.email, password)
    }

    @Test
    fun createUser_E2Invalid_errorOnAccountCreation(): Unit = runBlocking{
        userService.createUser(email, password)
        assertThrows<AccountAlreadyRegistredException> {
            userService.createUser(email, password)
        }
    }

    @Test
    fun login_E1Valid_userIsLogged(): Unit = runBlocking{
        val userTest: User = userService.createUser(email, password)
        assertEquals(true, userService.login(userTest.email, password))
        userService.deleteUser(userTest.email, password)
    }

    @Test
    fun login_E2Invalid_errorOnLogin(): Unit = runBlocking {
        assertThrows<UnregistredUserException>{
            userService.login(email, password)
        }
    }

    @Test
    fun viewUserData_E1Valid_userDataViewed(): Unit = runBlocking {
        val userData: Boolean = userService.viewUserData(email)
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
    fun editUserData_E1Valid_userDataEdited() {
        val newPassword = "GuillemElMejor"
        userService.editUserData(newPassword)
        assertEquals(true, newPassword)
    }

    @Test
    fun editUserData_E1Invalid_userDataEdited(){
        val newPassword = "J"
        assertThrows<IncorrectDataException>{
            userService.editUserData(newPassword)
        }
    }


    @Test
    fun signOut_E1Valid_userSignedOut(): Unit = runBlocking{
        val userTest: User = userService.createUser(email, password)
        userService.login(userTest.email, password)
        assertEquals(true, userService.signOut())
        userService.deleteUser(userTest.email, password)
    }

    @Test
    fun signOut_E2Invalid_errorSigningOut(): Unit = runBlocking{
        val userTest: User = userService.createUser(email, password)
        assertThrows<SessionNotStartedException>{
            userService.signOut()
        }
        userService.deleteUser(userTest.email, password)
    }

    @Test
    fun deleteUser_E1Valid_userIsDeleted(): Unit = runBlocking {
        val userTest: User = userService.createUser(email, password)
        val initialCount = db.getNumberUsers()
        userService.deleteUser(email, password)
        val finalCount = db.getNumberUsers()
        assertEquals(initialCount - 1, finalCount)
    }

    @Test
    fun deleteUser_E1Invalid_userIsDeleted(): Unit = runBlocking {
        val userTest: User = userService.createUser(email, password)
        assertThrows<UnableToDeleteUserException>{
            userService.deleteUser(userTest.email, password)
        }
    }
}