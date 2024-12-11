package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.IncorrectDataException
import uji.es.intermaps.Exceptions.SessionNotStartedException
import uji.es.intermaps.Exceptions.UnableToDeleteUserException
import uji.es.intermaps.Exceptions.UnregistredUserException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.User
import uji.es.intermaps.ViewModel.UserService

@RunWith(AndroidJUnit4::class)
class UserServiceTests {
    private lateinit var mockRepository: Repository
    private lateinit var userService: UserService

    private var email: String = "prueba@uji.es"
    private var password: String = "123456AA" // Cambiar en las pruebas de aceptacion para que cumpla los requisitos de las contraseñas
    private var userTest: User = User("emaildeprueba@gmail.com", "123456BB")

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        mockRepository = mock(Repository::class.java)
        userService = UserService(mockRepository)
    }

    @Test
    fun createUser_E1Valid_userIsCreated(): Unit = runBlocking {
        `when`(mockRepository.createUser(email, password)).thenReturn(User(email, password))
        val user = User(email,password)

        val res = userService.createUser(email, password)

        assertEquals(user.email,res.email )
        verify(mockRepository).createUser(anyString(), anyString())
    }

    @Test(expected = AccountAlreadyRegistredException::class)
    fun createUser_E2Invalid_errorOnAccountCreation(): Unit = runBlocking {
        doAnswer { throw AccountAlreadyRegistredException("Este usuario ya está registrado") }
            .`when`(mockRepository).createUser(anyString(), anyString())
        userService.createUser(userTest.email, userTest.pswd)
    }

    @Test
    fun login_E1Valid_userIsLogged(): Unit = runBlocking {
        `when`(mockRepository.loginUser(userTest.email, userTest.pswd)).thenReturn(true)

        val res = userService.login(userTest.email, userTest.pswd)

       assertEquals(true, res)
        verify(mockRepository).loginUser(userTest.email, userTest.pswd)
    }

    @Test(expected = UnregistredUserException::class)
    fun login_E2Invalid_errorOnLogin(): Unit = runBlocking {
        doAnswer { throw UnregistredUserException("Este usuario no está registrado") }
            .`when`(mockRepository).loginUser(anyString(), anyString())
        userService.login(userTest.email, userTest.pswd)
    }

    @Test
    fun viewUserData_E1Valid_userDataViewed(): Unit = runBlocking {
        `when`(mockRepository.viewUserData(userTest.email)).thenReturn(true)

        val userData = userService.viewUserData(userTest.email)

        assertEquals(true, userData)
        verify(mockRepository).viewUserData(userTest.email)
    }

    @Test(expected = SessionNotStartedException::class)
    fun viewUserData_E2Invalid_userDataNotViewed(): Unit = runBlocking {
        doAnswer { throw SessionNotStartedException("No se ha iniciado sesión") }
            .`when`(mockRepository).viewUserData(anyString())
        val emailError = "noexiste@gmail.com"
        userService.viewUserData(emailError)
    }

    @Test
    fun editUserData_E1Valid_userDataEdited(): Unit = runBlocking {
        `when`(mockRepository.editUserData(anyString())).thenReturn(true)
        val newPassword = "GuillemElMejor"
        val res = userService.editUserData(newPassword)
        assertEquals(true, res)
        verify(mockRepository).editUserData(newPassword)
    }

    @Test(expected = IncorrectDataException::class)
    fun editUserData_E2Invalid_userDataEdited(): Unit = runBlocking {
        doAnswer{ throw IncorrectDataException("La contraseña debe tener al menos 6 caracteres") }
            .`when`(mockRepository).editUserData(anyString())
        val newPassword = "J"
        userService.editUserData(newPassword)
    }


    @Test
    fun signOut_E1Valid_userSignedOut(): Unit = runBlocking {
        `when`(mockRepository.signOut()).thenReturn(true)
        val res = userService.signOut()
        assertEquals(true, res)
    }

    @Test(expected = SessionNotStartedException::class)
    fun signOut_E2Invalid_errorSigningOut(): Unit = runBlocking {
        doAnswer { throw SessionNotStartedException("No se ha iniciado sesión") }
            .`when`(mockRepository).signOut()
        userService.signOut()
    }

    @Test
    fun deleteUser_E1Valid_userIsDeleted(): Unit = runBlocking {
        `when`(mockRepository.deleteUser(email, password)).thenReturn(true)
        assertEquals(true, userService.deleteUser(email, password))
        verify(mockRepository).deleteUser(email, password)
    }

    @Test(expected = UnableToDeleteUserException::class)
    fun deleteUser_E2Invalid_userIsDeleted(): Unit = runBlocking {
        doAnswer{throw UnableToDeleteUserException("No se ha podido eliminar el usuario") }
            .`when`(mockRepository).deleteUser(email, password)
        userService.deleteUser(email, password)
    }


}