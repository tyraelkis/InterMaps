package acceptanceTests


import uji.es.intermaps.Exceptions.NotValidAliasException
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Model.User
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeAll
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Exceptions.IncorrectDataException
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.InterestPlaceService
import uji.es.intermaps.Model.Repository
import uji.es.intermaps.Model.UserService

@RunWith(AndroidJUnit4::class)
class AT_IT1 {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DataBase
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var user: User
    private lateinit var userService: UserService
    private lateinit var interestPlace: InterestPlace
    private lateinit var interestPlaceService: InterestPlaceService
    private lateinit var repository: Repository


    @BeforeAll
    fun setUp() {
        //variables base de datos
        auth = Firebase.auth
        db = DataBase
        repository = FirebaseRepository()
        //variables usuario
        email = "prueba@uji.es"
        password = "12345"
        user = User(email)
        userService = UserService(repository)
        //variables lugar de interés
        interestPlace = InterestPlace(Coordinate(-18.665695, 35.529562), "Mozambique", "Moz", false)
        interestPlaceService = InterestPlaceService(repository)
    }

    @Test
    fun createUser_E1Valid_userIsCreated() { //Ver si es mejor que devuelva un booleano y sin excepciones o que es gestionen en el método de la clase
        val initialCount = db.getNumberUsers()
        user.createUser()
        val finalCount = db.getNumberUsers()
        assertEquals(initialCount + 1, finalCount)
        user.deleteUser()
    }

    @Test
    fun createUser_E2Invalid_errorOnAccountCreation() { //Revisar la prueba. Además mirar si asi vale o hay que usar AfterEach para revertir las pruebas
        user.createUser()
        try {
            user.createUser()
            fail("No se debería poder crear el usuario")
        }catch (e: AccountAlreadyRegistredException){
            assertEquals("Ya existe el usuario", e.message)
        }
        user.deleteUser()
    }

    @Test
    fun login_E1Valid_userIsLogged() {
        user.createUser()
        assertEquals(true, user.login())
        user.deleteUser()
    }

    @Test
    fun login_E2Invalid_errorOnLogin() { //Preguntar a Miguel
        assertEquals(false, user.login())
        /*
        try {
            user.login()
            fail("No se debería poder iniciar sesión")
        }catch (e: UnregistredUserException){
            assertEquals("No existe el usuario", e.message)
        }
        */
    }

    @Test
    fun viewUserData_E1Valid_userDataViewed() {
        val expectedEmail = "juan@ejemplo.com"
        val expectedPassword = "123456"
        user.viewUserData()
        assertEquals(expectedEmail, user.email)
        assertEquals(expectedPassword, user.password)
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
        try {
            userService.editUserData(email, newPassword)
            assert(false){"Se esperaba excepción"}
        }catch (e: IncorrectDataException){
            assertEquals("La contraseña no cumple con los requisitos", e.message)
        }
    }


    @Test
    fun signOut_E1Valid_userSignedOut() {
        user.createUser()
        user.login()
        assertEquals(true, user.signOut())
        user.deleteUser()
    }

    @Test
    fun signOut_E2Invalid_errorSigningOut() {
        user.createUser()
        assertEquals(false, user.signOut())
        user.deleteUser()
    }

    @Test
    fun deleteUser_E1Valid_userIsDeleted() {
        val initialCount = db.getNumberUsers()
        user.deleteUser()
        val finalCount = db.getNumberUsers()
        assertEquals(initialCount - 1, finalCount)
    }

    @Test
    fun createInterestPlace_E1Valid_InterestPlaceCreated() {
        assertEquals(true, interestPlace.createInterestPlace())
        interestPlace.deleteInterestPlace()
    }

    @Test
    fun createInterestPlace_E2Invalid_errorOnCreatingInterestPlace() {
        assertEquals(false, interestPlace.createInterestPlace())
        interestPlace.deleteInterestPlace()
    }

    @Test
    fun editInterestPlace_E1Valido_setAliasToAPlaceOfInterest() {
        val result: Boolean = interestPlaceService.setAlias(interestPlace, newAlias = "Mozambiquinho")
        assertEquals(true, result)
    }

    @Test
    fun editInterestPlace_E1Invalido_errorSetAliasToAPlaceOFInterest(){
        try{
            interestPlaceService.setAlias(interestPlace, newAlias = "@#//")
            assert(false){"se esperaba excepción"}
        }catch (e: NotValidAliasException){
            assertEquals("El nuevo alias no es valido", e.message)
        }
    }

}