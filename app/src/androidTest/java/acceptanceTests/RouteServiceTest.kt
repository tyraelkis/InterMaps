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
    var routeTest: Route? = null

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
        val vehicleType = routeService.getVehicleTypeAndConsump(routeTest).first
        routeService.calculateConsumition(routeTest, routeTest.trasnportMethod, vehicleType)
        val res = db.doesRouteExist(routeTest)
        routeService.deleteRoute(routeTest.origin, routeTest.destination, routeTest.trasnportMethod)
        assertEquals(true, res)
    }

    @Test(expected = NotValidPlaceException::class)
    fun createRoute_E4Invalid_routeNotCreated(): Unit = runBlocking {
        routeService.createRoute("Borriol", "Madrid", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON")

    }

    @Test
    fun calculateConsumition_E1Valid_consumitionCalculated(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO,
            RouteTypes.RAPIDA, "9999GON")
        val vehicleType = routeService.getVehicleTypeAndConsump(routeTest).first
        val calculatedConsumition = routeService.calculateConsumition(routeTest, TransportMethods.VEHICULO, vehicleType)
        val result = 9.12
        assertEquals(result, calculatedConsumition, 0.1)
    }

    @Test(expected = NotValidTransportException::class)
    fun calculateConsumition_E2Invalid_consumitionNotCalculated(): Unit = runBlocking {
        val routeTest = routeService.createRoute("Galicia", "Castellón", TransportMethods.APIE,RouteTypes.RAPIDA, "1111AAA")
        routeService.calculateConsumition(routeTest, TransportMethods.APIE, VehicleTypes.ELECTRICO)

    }

    @Test
    fun calculateCaloriesConsumition_E1Valid_consumitionCalculated(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Burriana", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        val calculatedConsumition = routeService.calculateCaloriesConsumition(routeTest, TransportMethods.APIE)
        val result = 13731.8
        assertEquals(result, calculatedConsumition, 0.1)
    }

    @Test (expected = NotValidTransportException::class)
    fun calculateCaloriesConsumition_E2Invalid_consumitionNotCalculated(): Unit = runBlocking {
        val routeTest = routeService.createRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        routeService.calculateCaloriesConsumition(routeTest, TransportMethods.VEHICULO)

    }

    @Test
    fun saveRoute_E1Valid_routeSaved(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Castello de la Plana, VC, Spain", "Borriana, VC, Spain", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON")
        val res = routeService.putRoute(routeTest)
        assertEquals(true, res)
    }

    @Test (expected = NotValidPlaceException::class)
    fun saveRoute_E2Invalid_noRouteSaved(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Valencia", "Valencia", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON")
        routeService.putRoute(routeTest)
    }




}