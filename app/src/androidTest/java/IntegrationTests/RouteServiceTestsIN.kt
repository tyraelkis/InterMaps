package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.APIParsers.RouteGeometry
import uji.es.intermaps.APIParsers.RouteProperties
import uji.es.intermaps.APIParsers.RouteSummary
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
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
class RouteTests {



    var mockRepository: Repository = mock(Repository::class.java)


    var mockRouteRepository: RouteRepository = mock(RouteRepository::class.java)

    var mockInterestPlaceService: InterestPlaceService = mock(InterestPlaceService::class.java)

    private val userService = UserService(FirebaseRepository())
    private val userTest: User = User("emaildeprueba@gmail.com", "123456BB")
    lateinit private var routeService: RouteService
    private val route = Route("","", emptyList(), 0.0, 0.0.toString(),0.0,  RouteTypes.RAPIDA, false, TransportMethods.VEHICULO, "")

    @Before
    fun setup():Unit = runBlocking() {
        userService.login(userTest.email, userTest.pswd)
        routeService = RouteService(mockRepository)
        routeService.routeRepository = mockRouteRepository // Usamos el mock para la base de datos
    }

    @After
    fun tearDown():Unit = runBlocking() {
        userService.signOut()
    }

    @Test
    fun createRoute_E1Valid_routeIsCreated():Unit = runBlocking() {
        // Mockear la creación de la ruta
        var mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            route = emptyList(),
            distance = 0.0,
            duration = 0.0.toString(),
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "",
        )

        val mockedCall = RouteFeature(
            geometry = RouteGeometry(
                coordinates = emptyList()
            ),
            properties = RouteProperties(
                RouteSummary(
                    distance = 0.0,
                    duration = 0.0
                )
            ),
        )

        `when`(mockRouteRepository.searchInterestPlaceByToponym("Burriana"))
            .thenReturn(
                InterestPlace(
                    Coordinate(39.888399, -0.085748),
                    toponym = "Burriana",""
                )
            )
        `when`(mockRouteRepository.searchInterestPlaceByToponym("Castellón de la Plana"))
            .thenReturn(
                InterestPlace(
                    Coordinate(39.987142, -0.037787),
                    toponym = "Castellón de la Plana",""
                )
            )
        `when`(mockRouteRepository.calculateRoute("-0.085748,39.888399", "-0.037787,39.987142", transportMethod = TransportMethods.VEHICULO,RouteTypes.RAPIDA))
            .thenReturn(mockedCall)


        `when`(mockRouteRepository.createRoute(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            routeType = RouteTypes.RAPIDA,
            vehiclePlate = "",
            route = mockedCall
        )).thenReturn(mockedRoute)

        val routeTest = routeService.createRoute("Burriana", "Castellón de la Plana", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON")
        // Comprobamos que la ruta fue creada correctamente
        assertEquals(mockedRoute, routeTest)
        verify(mockRouteRepository).searchInterestPlaceByToponym("Burriana")
        verify(mockRouteRepository).searchInterestPlaceByToponym("Castellón de la Plana")
        verify(mockRouteRepository).calculateRoute("-0.085748,39.888399", "-0.037787,39.987142", transportMethod = TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        verify(mockRouteRepository).createRoute(origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            routeType = RouteTypes.RAPIDA,
            vehiclePlate = "",
            route = mockedCall
        )


    }

    @Test
    fun saveRoute_E2Valid_routeSaved(): Unit = runBlocking {
        val mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            route = emptyList(),
            distance = 25.0,
            duration = "30 min",
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "1234XYZ"
        )

        `when`(mockRepository.saveRouteToDatabase(mockedRoute)).thenReturn(Unit)
        routeService.putRoute(mockedRoute)
        verify(mockRepository).saveRouteToDatabase(mockedRoute)

    }

    @Test
    fun calculateFuelConsumition_E2Valid_consumitionCalculated(): Unit = runBlocking {
        val mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            route = emptyList(),
            distance = 25.0,
            duration = "30 min",
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "1234XYZ"
        )

        val vehicleType = VehicleTypes.GASOLINA
        val consumPerKm = 7.0

        val expectedCost = mockedRoute.distance * consumPerKm
        `when`(mockRouteRepository.calculateConsumition(mockedRoute, TransportMethods.VEHICULO, vehicleType))
            .thenReturn(expectedCost)

        val consumition = routeService.calculateConsumition(mockedRoute, TransportMethods.VEHICULO, vehicleType)

        assertEquals(expectedCost, consumition, 0.1)
        verify(mockRouteRepository).calculateConsumition(mockedRoute, TransportMethods.VEHICULO, vehicleType)
    }

    @Test
    fun calculateCaloriesConsumition_E3Valid_consumitionCalculated(): Unit = runBlocking {
        val mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.APIE,
            route = emptyList(),
            distance = 10.0, // 10 km de distancia como ejemplo
            duration = "2 h",
            cost = 0.0, // Inicialmente 0, se calculará en el test
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = ""
        )

        val expectedCalories = mockedRoute.distance * 62.0
        `when`(mockRouteRepository.calculateCaloriesConsumition(mockedRoute, TransportMethods.APIE))
            .thenReturn(expectedCalories)

        val calories = routeService.calculateCaloriesConsumition(mockedRoute, TransportMethods.APIE)

        assertEquals(expectedCalories, calories, 0.1)
        verify(mockRouteRepository).calculateCaloriesConsumition(mockedRoute, TransportMethods.APIE)

    }

    @Test
    fun createRouteWithType_E1Valid_routeIsCalculated(): Unit = runBlocking {
        val mockedCall = RouteFeature(
            geometry = RouteGeometry(
                coordinates = emptyList()
            ),
            properties = RouteProperties(
                RouteSummary(
                    distance = 1200.0,
                    duration = 1100.0
                )
            ),
        )
        `when`(mockRouteRepository.calculateRoute("Burriana", "Castellón", TransportMethods.VEHICULO,RouteTypes.CORTA))
            .thenReturn(mockedCall)

        val routeTest = routeService.createTypeRoute("Burriana", "Castellón", TransportMethods.VEHICULO,RouteTypes.CORTA)

        assertEquals(Pair(true, mockedCall), routeTest)

        verify(mockRouteRepository).calculateRoute("Burriana", "Castellón", TransportMethods.VEHICULO,RouteTypes.CORTA)
    }

    @Test
    fun viewRouteList_E1Valido_routeListViewed(): Unit = runBlocking{
        `when`(mockRepository.viewRouteList()).thenReturn(listOf(route))
        val res = routeService.viewRouteList()
        assertTrue(res.isNotEmpty())
        verify(mockRepository).viewRouteList()
    }

    @Test
    fun viewRouteList_E2Valido_emptyRouteListViewed(): Unit = runBlocking{
        `when`(mockRepository.viewRouteList()).thenReturn(emptyList())
        val res = routeService.viewRouteList()
        assertTrue(res.isEmpty())
        verify(mockRepository).viewRouteList()
    }
}