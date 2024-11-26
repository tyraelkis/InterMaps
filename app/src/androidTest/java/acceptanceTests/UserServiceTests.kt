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
    fun createUser_E1Valid_userIsCreated() = runBlocking {
        val userTest: User = userService.createUser(email, password)
        assertEquals(true, db.doesUserExist(userTest.email))
        userService.deleteUser(userTest.email)
    }

    @Test
    fun createUser_E2Invalid_errorOnAccountCreation(): Unit = runBlocking{
        assertThrows<AccountAlreadyRegistredException> {
            userService.createUser(email, password)
        }
    }

    @Test
    fun login_E1Valid_userIsLogged() = runBlocking{
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
    fun signOut_E1Valid_userSignedOut() = runBlocking{
        val userTest: User = userService.createUser(email, password)
        userService.login(userTest.email, userTest.password)
        assertEquals(true, userService.signOut(userTest.email,userTest.password))
        userService.deleteUser(userTest.email)
    }

    @Test
    fun signOut_E2Invalid_errorSigningOut() = runBlocking{
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
}