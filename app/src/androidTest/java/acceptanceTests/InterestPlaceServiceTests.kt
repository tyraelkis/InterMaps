package acceptanceTests

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.User
import uji.es.intermaps.ViewModel.UserService

class InterestPlaceServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var interestPlace: InterestPlace = InterestPlace(Coordinate(-18.665695, 35.529562), "Mozambique", "Moz", false)
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    private var email: String = "emaildeprueba@gmail.com" //Usuario con lista de lugares. Hay que añadirle un lugar
    private var emailEmpty: String = "emaildepruebaempty@gmail.com" //Usuario sin lista de lugares

    //Crear valores por defecto para pruebas de create y delete?

    /* //TODO Preguntar porque puede ser que no se mantenga la sesión iniciada
    companion object {
        private val userService = UserService(FirebaseRepository())
        private val userTest: User = User("emaildeprueba@gmail.com", "123456BB")

        @JvmStatic
        @BeforeAll
        fun setup(): Unit = runBlocking {
            userService.login(userTest.email, userTest.pswd)
        }

        @JvmStatic
        @AfterAll
        fun tearDown(): Unit = runBlocking {
            userService.signOut()
        }
    }
    */

    @Test
    fun createInterestPlace_E1Valid_InterestPlaceCreated(): Unit = runBlocking{
        val interestPlaceTest : InterestPlace = interestPlaceService.createInterestPlaceCoordinates(Coordinate(-16.665695, 36.529562)) //Cambiar coordenadas segun los tests
        val res = db.doesInteresPlaceExists(interestPlaceTest.coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceTest.coordinate)
        assertEquals(true, res)
    }

    @Test
    fun createInterestPlace_E2Invalid_errorOnCreatingInterestPlace(): Unit = runBlocking {
        assertThrows<NotValidCoordinatesException>{
            interestPlaceService.createInterestPlaceCoordinates(Coordinate(-1800.665695,35.529562))
        }
    }

    @Test
    fun searchInterestPlaceByCoordinate_E1Valid_InterestPlaceFound(): Unit = runBlocking {
        val res: InterestPlace = interestPlaceService.searchInterestPlaceByCoordiante(interestPlace.coordinate)
        val resultado : Boolean = res.toponym.contains(interestPlace.toponym)
        assertEquals(true, resultado)
    }

    @Test
    fun searchInterestPlaceByCoordinate_E2Invalid_errorOnSearchingInterestPlace(): Unit = runBlocking {
        assertThrows<NotValidCoordinatesException>{
            interestPlaceService.searchInterestPlaceByCoordiante(Coordinate(-300.0,300.0))
        }
    }

    @Test
    fun viewInterestPlaceData_E1Valid_InterestPlaceDataViewed(): Unit = runBlocking {
        val res : Boolean = interestPlaceService.viewInterestPlaceData(interestPlace.coordinate)
        assertEquals(true, res)
    }

    @Test
    fun viewInterestPlaceData_E2Invalid_errorOnViewingInterestPlaceData(): Unit = runBlocking { //Busca en la lista de lugares
        assertThrows<NotSuchPlaceException>{
            interestPlaceService.viewInterestPlaceData(Coordinate(-19.665695,35.529562))
        }
    }
    
    @Test
    fun editInterestPlace_E1Valido_setAliasToAPlaceOfInterest(): Unit = runBlocking {
        val result: Boolean = interestPlaceService.setAlias(interestPlace, newAlias = "Mozambiquinho")
        assertEquals(true, result)
    }

    @Test
    fun editInterestPlace_E2Invalido_errorSetAliasToAPlaceOFInterest(): Unit = runBlocking{
        assertThrows<NotValidAliasException>{
            interestPlaceService.setAlias(interestPlace, newAlias = "@#//")
        }
    }

    @Test
    fun searchInterestPlaceByToponym_E1Valido_InterestPlaceFound(): Unit = runBlocking { //Cambiar para que devuelva un interestplace
        val res: Boolean = interestPlaceService.searchInterestPlaceByToponym(interestPlace.toponym)
        assertEquals(true, res)
    }

    @Test
    fun searchInterestPlaceByToponym_E2Invalido_errorOnSearchingInterestPlaceByToponym(): Unit = runBlocking {
        assertThrows<NotSuchPlaceException>{
            interestPlaceService.searchInterestPlaceByToponym("Roshar")
        }
    }

    @Test
    fun viewInterestPlaceList_E1Valido_InterestPlaceListViewed(): Unit = runBlocking{
        val res = interestPlaceService.viewInterestPlaceList(email)
        assertTrue(res.isNotEmpty())
    }

    @Test
    fun viewInterestPlaceList_E2Invalido_emptyInterestPlaceListViewed(): Unit = runBlocking{
        val res = interestPlaceService.viewInterestPlaceList(emailEmpty)
        assertTrue(res.isEmpty())
    }
}