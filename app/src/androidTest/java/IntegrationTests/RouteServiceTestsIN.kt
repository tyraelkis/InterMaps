package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.APIParsers.RouteGeometry
import uji.es.intermaps.APIParsers.RouteProperties
import uji.es.intermaps.APIParsers.RouteSummary
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.User
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
            trasnportMethod = TransportMethods.VEHICULO,
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
        `when`(mockRouteRepository.calculateRoute("-0.085748,39.888399", "-0.037787,39.987142", trasnportMethod = TransportMethods.VEHICULO,RouteTypes.RAPIDA))
            .thenReturn(mockedCall)


        `when`(mockRepository.createRoute(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            routeType = RouteTypes.RAPIDA,
            vehiclePlate = "",
            routeFeature = mockedCall
        ))
            .thenReturn(mockedRoute)

        val routeTest = routeService.createRoute("Burriana", "Castellón de la Plana", TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        // Comprobamos que la ruta fue creada correctamente
        assertEquals(mockedRoute, routeTest)
        verify(mockRouteRepository).searchInterestPlaceByToponym("Burriana")
        verify(mockRouteRepository).searchInterestPlaceByToponym("Castellón de la Plana")
        verify(mockRouteRepository).calculateRoute("-0.085748,39.888399", "-0.037787,39.987142", trasnportMethod = TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        verify(mockRepository).createRoute(origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            routeType = RouteTypes.RAPIDA,
            vehiclePlate = "",
            routeFeature = mockedCall
        )


    }

    @Test
    fun calculateFuelConsumition_E2Valid_consumitionCalculated():Unit = runBlocking() {
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

        var mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón",
            trasnportMethod = TransportMethods.VEHICULO,
            route = emptyList(),
            distance = 0.0,
            duration = 0.0.toString(),
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "",
        )

        `when`(mockRepository.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "", mockedCall))
            .thenReturn(mockedRoute)

        val routeTest = routeService.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        val consumition = routeTest.cost

        verify(mockRepository).createRoute("Burriana", "Castellón", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "",mockedCall)

        // Comprobamos que la ruta fue creada correctamente
        assertEquals(mockedRoute.cost, consumition)


    }

    @Test
    fun calculateCaloriesConsumition_E3Valid_consumitionCalculated():Unit = runBlocking() {
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

        var mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón",
            trasnportMethod = TransportMethods.APIE,
            route = emptyList(),
            distance = 0.0,
            duration = 0.0.toString(),
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "",
        )

        `when`(mockRepository.createRoute("Burriana", "Castellón", TransportMethods.APIE,RouteTypes.RAPIDA, "", mockedCall))
            .thenReturn(mockedRoute)

        val routeTest = routeService.createRoute("Burriana", "Castellón", TransportMethods.APIE, RouteTypes.RAPIDA)
        val consumition = routeTest.cost

        verify(mockRepository).createRoute("Burriana", "Castellón", TransportMethods.APIE,RouteTypes.RAPIDA, "", mockedCall)

        // Comprobamos que la ruta fue creada correctamente
        assertEquals(mockedRoute.cost, consumition)


    }
}