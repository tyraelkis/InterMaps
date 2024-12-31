package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Exceptions.NoValidTypeException
import uji.es.intermaps.Exceptions.NotSuchElementException
import uji.es.intermaps.Exceptions.NotValidPlaceException
import uji.es.intermaps.Exceptions.NotValidTransportException
import uji.es.intermaps.Interfaces.ProxyService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.CachePrecioLuz
import uji.es.intermaps.Model.ConsultorPreciLuz
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.VehicleTypes
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.ViewModel.RouteService
import uji.es.intermaps.ViewModel.UserService
import uji.es.intermaps.ViewModel.VehicleService

@RunWith(AndroidJUnit4::class)
class RouteServiceTest {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var email: String = "prueba@uji.es"
    private var password: String = "123456AA"
    private var userService: UserService = UserService(repository)
    private var userTest: User = User("emaildeprueba@gmail.com", "123456BB")
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    private val servicioLuz: ProxyService = CachePrecioLuz(ConsultorPreciLuz())
    private var routeService: RouteService = RouteService(repository, servicioLuz)
    private var vehicleService: VehicleService = VehicleService(repository)
    var routeTest: Route? = null
    private var emailEmpty: String = "emaildepruebaempty@gmail.com" //Usuario sin lista de lugares


    @Before
    fun setup(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        interestPlaceService.createInterestPlaceFromToponym("Valencia")
        interestPlaceService.createInterestPlaceFromToponym("Vila-real")
        interestPlaceService.createInterestPlaceFromToponym("Burriana")
        interestPlaceService.createInterestPlaceFromToponym("Castellón de la Plana")
        vehicleService.createVehicle("9999GON", "gasolina",9.0)
    }

    @After
    fun tearDown(): Unit = runBlocking {
        interestPlaceService.deleteInterestPlace(interestPlaceService.getInterestPlaceByToponym("Valencia").coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceService.getInterestPlaceByToponym("Vila-real").coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceService.getInterestPlaceByToponym("Burriana").coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceService.getInterestPlaceByToponym("Castellón de la Plana").coordinate)
        vehicleService.deleteVehicle("9999GON")
        userService.signOut()
    }

    @Test
    fun createRoute_E1Valid_routeIsCreated(): Unit = runBlocking {
        val res = routeService.createRoute("Burriana", "Castellón de la Plana", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON").first
        assertEquals(true, res)
    }

    @Test(expected = NotValidPlaceException::class)
    fun createRoute_E4Invalid_routeNotCreated(): Unit = runBlocking {
        routeService.createRoute("Borriol", "Madrid", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON")
    }

    @Test
    fun calculateConsumition_E4Valid_consumitionCalculated(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Burriana", "Castellón de la Plana", TransportMethods.VEHICULO,
            RouteTypes.RAPIDA, "9999GON").second
        val vehicleType = routeService.getVehicleTypeAndConsump(routeTest).first
        val calculatedConsumition = routeService.calculateConsumition(routeTest, TransportMethods.VEHICULO, vehicleType)
        val result = 1.928
        assertEquals(result, calculatedConsumition, 0.1)
    }

    @Test(expected = NotValidTransportException::class)
    fun calculateConsumition_E4Invalid_consumitionNotCalculated(): Unit = runBlocking {
        val routeTest = routeService.createRoute("Galicia", "Castellón de la Plana", TransportMethods.APIE,RouteTypes.RAPIDA, "1111AAA").second
        routeService.calculateConsumition(routeTest, TransportMethods.APIE, VehicleTypes.ELECTRICO)

    }

    @Test
    fun calculateCaloriesConsumition_E4Valid_consumitionCalculated(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Burriana", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "").second
        val calculatedConsumition = routeService.calculateCaloriesConsumition(routeTest, TransportMethods.APIE)
        val result = 13835.9
        assertEquals(result, calculatedConsumition, 0.1)
    }

    @Test (expected = NotValidTransportException::class)
    fun calculateCaloriesConsumition_E4Invalid_consumitionNotCalculated(): Unit = runBlocking {
        val routeTest = routeService.createRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "").second
        routeService.calculateCaloriesConsumition(routeTest, TransportMethods.VEHICULO)

    }

    @Test
    fun saveRoute_E4Valid_routeSaved(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Castellón de la Plana", "Burriana", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON").second
        val res = routeService.putRoute(routeTest)
        assertEquals(true, res)
        routeService.deleteRoute(routeTest)
    }

    @Test (expected = NotValidPlaceException::class)
    fun saveRoute_E4Invalid_noRouteSaved(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Valencia", "Valencia", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON").second
        routeService.putRoute(routeTest)
    }

    @Test
    fun deleteRoute_E4Valid_routeDeleted(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Castellón de la Plana", "Burriana", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON").second
        routeService.putRoute(routeTest)
        val res = routeService.deleteRoute(routeTest)
        assertEquals(true, res)
    }

    @Test (expected = NotValidPlaceException::class)
    fun deleteRoute_E4Invalid_routeDeleted(): Unit = runBlocking {
        val routeTest: Route = routeService.createRoute("Castellón", "Castellón", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON").second
        routeService.deleteRoute(routeTest)
    }
    @Test
    fun viewRouteList_E1Valido_RouteListViewed(): Unit = runBlocking{
        val routeTest: Route = routeService.createRoute("Castellón de la Plana", "Burriana", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON").second
        routeService.putRoute(routeTest)
        val res = routeService.viewRouteList()
        assertTrue(res.isNotEmpty())
        routeService.deleteRoute(routeTest)

    }

    @Test
    fun viewRouteList_E2Valido_emptyRouteListViewed(): Unit = runBlocking{
        userService.signOut()
        userService.login(emailEmpty, "123456BB")
        val res = routeService.viewRouteList()
        userService.signOut()
        userService.login(userTest.email, userTest.pswd)
        assertTrue(res.isEmpty())
    }


    @Test
    fun createRouteWithType_E1Valid_routeIsCalculated(): Unit = runBlocking {
        val routeTest = routeService.createTypeRoute("-0.085748,39.888399", "-0.037787,39.987142", TransportMethods.VEHICULO,RouteTypes.CORTA)
        assertEquals(true, routeTest.first)
    }

    @Test(expected = NoValidTypeException::class)
    fun createRouteWithType_E2Invalid_routeNotCalculated(): Unit = runBlocking {
        routeService.createTypeRoute("-0.085748,39.888399", "-0.037787,39.987142", TransportMethods.VEHICULO,null)
    }


    @Test
    fun setFavRoute_E1Valid_routeIsFav(): Unit = runBlocking {
        val result: Boolean = routeService.setFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        assertEquals(true, result)
    }

    @Test (expected = NotSuchElementException::class)
    fun setFavRoute_E3Invalid_errorOnSettingFavRoute(): Unit = runBlocking {
        routeService.setFavRoute("Burriana", "Castellón de la Plana", TransportMethods.VEHICULO, RouteTypes.RAPIDA, "9999GON")
    }

    @Test
    fun deleteFavRoute_E1Valid_routeDeleteAsFavourite(): Unit = runBlocking {
        val result: Boolean = routeService.deleteFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        assertEquals(true, result)
    }

    @Test (expected = NotSuchElementException::class)
    fun deleteFavRoute_E3Invalid_errorOnDeletingFavRoute(): Unit = runBlocking {
        routeService.deleteFavRoute("Burriana", "Castellón de la Plana", TransportMethods.VEHICULO, RouteTypes.RAPIDA, "9999GON")
    }

}