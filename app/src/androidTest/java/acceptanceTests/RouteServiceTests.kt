package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.TrasnportMethods
import uji.es.intermaps.Model.User
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.ViewModel.RouteService
import uji.es.intermaps.ViewModel.UserService

@RunWith(AndroidJUnit4::class)
class RouteServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var email: String = "prueba@uji.es"
    private var password: String = "123456AA" // Cambiar en las pruebas de aceptacion para que cumpla los requisitos de las contraseñas
    private var userService: UserService = UserService(repository)
    private var userTest: User = User("emaildeprueba@gmail.com", "123456BB")
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    private var routeService: RouteService = RouteService(repository)

    @Before
    fun setup(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
    }
    @After
    fun tearDown(): Unit = runBlocking {
        userService.signOut()
    }

    @Test
    fun createRoute_E1Valid_routeIsCreated(): Unit = runBlocking {
        interestPlaceService.createInterestPlaceFromToponym("Burriana")
        interestPlaceService.createInterestPlaceFromToponym("Castellón")
        val routeTest: Route = routeService.createRoute("Burriana", "Castellón", TrasnportMethods.VEHICULO)
        val res = db.doesRouteExist(routeTest)
        routeService.deleteRoute(routeTest.origin, routeTest.destination, routeTest.trasnportMethod)
        assertEquals(true, res)
    }

    @Test (expected = NotValidPlaceException::class)
    fun createRoute_E4Invalid_routeNotCreated(): Unit = runBlocking {
        routeService.createRoute("Borriol", "Madrid", TrasnportMethods.VEHICULO)
    }

}