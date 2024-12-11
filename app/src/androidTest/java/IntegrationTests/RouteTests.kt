package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import uji.es.intermaps.Interfaces.Repository
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
open class RouteTests {



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
            destination = "Castellón",
            trasnportMethod = TransportMethods.VEHICULO,
            route = emptyList(),
            distance = 0.0,
            duration = 0.0,
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "",
        )

        `when`(mockRepository.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO))
            .thenReturn(mockedRoute)

        val routeTest = routeService.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO)

        verify(mockRepository).createRoute("Burriana", "Castellón", TransportMethods.VEHICULO)

        // Comprobamos que la ruta fue creada correctamente
        assertEquals(mockedRoute, routeTest)


    }

    @Test
    fun calculateFuelConsumition_E2Valid_consumitionCalculated():Unit = runBlocking() {

        var mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón",
            trasnportMethod = TransportMethods.VEHICULO,
            route = emptyList(),
            distance = 0.0,
            duration = 0.0,
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "",
        )

        `when`(mockRepository.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO))
            .thenReturn(mockedRoute)

        val routeTest = routeService.createRoute("Burriana", "Castellón", TransportMethods.VEHICULO)
        val consumition = routeTest.cost

        verify(mockRepository).createRoute("Burriana", "Castellón", TransportMethods.VEHICULO)

        // Comprobamos que la ruta fue creada correctamente
        assertEquals(mockedRoute.cost, consumition)


    }
}