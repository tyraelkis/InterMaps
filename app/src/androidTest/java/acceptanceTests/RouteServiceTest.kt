package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Exceptions.NotValidTransportException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.VehicleTypes
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.ViewModel.RouteRepository
import uji.es.intermaps.ViewModel.RouteService
import uji.es.intermaps.ViewModel.UserService

@RunWith(AndroidJUnit4::class)
class RouteServiceTest {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var email: String = "prueba@uji.es"
    private var password: String = "123456AA" // Cambiar en las pruebas de aceptacion para que cumpla los requisitos de las contraseñas
    private var userService: UserService = UserService(repository)
    private var userTest: User = User("emaildeprueba@gmail.com", "123456BB")
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    private var routeService: RouteService = RouteService(repository)
    private var routeRepository: RouteRepository = RouteRepository()

    @Before
    fun setup(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        interestPlaceService.createInterestPlaceFromToponym("Valencia")
        interestPlaceService.createInterestPlaceFromToponym("Vila-real")
    }
    @After
    fun tearDown(): Unit = runBlocking {
        interestPlaceService.deleteInterestPlace(interestPlaceService.getInterestPlaceByToponym("Valencia").coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceService.getInterestPlaceByToponym("Vila-real").coordinate)
        userService.signOut()
    }

    @Test
    fun createRoute_E1Valid_routeIsCreated(): Unit = runBlocking {
        interestPlaceService.createInterestPlaceFromToponym("Burriana")
        interestPlaceService.createInterestPlaceFromToponym("Castellón")
        val routeTest: Route = routeService.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON")
        val res = db.doesRouteExist(routeTest)
        assertEquals(true, res)
    }

    @Test(expected = NotValidPlaceException::class)
    fun createRoute_E4Invalid_routeNotCreated(): Unit = runBlocking {
        routeService.createRoute("Borriol", "Madrid", TransportMethods.VEHICULO,RouteTypes.RAPIDA)

    }

    @Test
    fun calculateFuelConsumition_E4Valid_consumitionCalculated(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO,
            RouteTypes.RAPIDA)
        val calculatedConsumition = routeService.calculateFuelConsumition(routeTest, TransportMethods.VEHICULO, VehicleTypes.GASOLINA)
        assertEquals(true, calculatedConsumition)
    }

    @Test(expected = NotValidTransportException::class)
    fun calculateFuelConsumition_E4Invalid_consumitionNotCalculated(): Unit = runBlocking {
        val routeTest = routeService.createRoute("Galicia", "Castellón", TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        routeService.calculateFuelConsumition(routeTest, TransportMethods.APIE, VehicleTypes.ELECTRICO)

    }

    @Test
    fun calculateElectricConsumition_E4Valid_consumitionCalculated(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Burriana", "Valencia", TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        val calculatedConsumition = routeService.calculateElectricConsumition(routeTest, TransportMethods.VEHICULO, VehicleTypes.ELECTRICO)
        assertEquals(true, calculatedConsumition)
    }

    @Test(expected = NotValidTransportException::class)
    fun calculateElectricConsumition_E4Invalid_consumitionNotCalculated(): Unit = runBlocking {
        val routeTest = routeService.createRoute("Galicia", "Valencia", TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        routeService.calculateElectricConsumition(routeTest, TransportMethods.APIE, VehicleTypes.ELECTRICO)

    }
    @Test
    fun calculateCaloriesConsumition_E4Valid_consumitionCalculated(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Burriana", "Alicante", TransportMethods.VEHICULO)
        val calculatedConsumition = routeService.calculateCaloriesConsumition(routeTest, TransportMethods.VEHICULO)
        assertEquals(true, calculatedConsumition)
    }

    @Test (expected = NotValidTransportException::class)
    fun calculateCaloriesConsumition_E4Invalid_consumitionNotCalculated(): Unit = runBlocking {
        val routeTest = routeService.createRoute("Galicia", "Alicante", TransportMethods.APIE)
        routeService.calculateCaloriesConsumition(routeTest, TransportMethods.VEHICULO)

    }

}