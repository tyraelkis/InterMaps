package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Mock

import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.User
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.ViewModel.RouteRepository
import uji.es.intermaps.ViewModel.UserService

@RunWith(AndroidJUnit4::class)
class InterestPlaceServiceTests {

    var mockRepository: Repository = mock(Repository::class.java)

    @Mock
    var  mockRouteRepository: RouteRepository = mock(RouteRepository::class.java)


    private lateinit var interestPlaceService: InterestPlaceService
    private val userService = UserService(FirebaseRepository())
    private val userTest: User = User("emaildeprueba@gmail.com", "123456BB")


    @Before
    fun setup(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
        interestPlaceService = InterestPlaceService(mockRepository)
        interestPlaceService.routeRepository = mockRouteRepository
    }

    @After
    fun tearDown(): Unit = runBlocking {
        userService.signOut()
    }



    @Test
    fun createInterestPlaceByToponym_E1Valido_InterestPlaceCreated(): Unit = runBlocking {
        // Mockear los datos de prueba
        val coordinate = Coordinate(-18.665695, 35.529562)
        val toponym = "Mozambique"
        val interestPlace = InterestPlace(coordinate, toponym, "")


        // Configurar el comportamiento de los mocks
        `when`(mockRepository.createInterestPlace(coordinate, toponym, "")).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )
        `when`(mockRouteRepository.searchInterestPlaceByToponym(toponym)).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )
        val interestPlaceTest: InterestPlace = interestPlaceService.createInterestPlaceFromToponym(toponym)

        verify(mockRepository).createInterestPlace(coordinate, toponym, "")
        verify(mockRouteRepository).searchInterestPlaceByToponym(toponym)


        assertEquals(interestPlace, interestPlaceService.createInterestPlaceFromToponym(toponym))
    }
}