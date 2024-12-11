package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException

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

    @Mock
    var mockRepository: Repository = mock(Repository::class.java)
    @Mock
    var  mockRouteRepository: RouteRepository = mock(RouteRepository::class.java)
    private val coordinate = Coordinate(-18.665695, 35.529562)
    private val toponym = "Mozambique"
    private val interestPlace = InterestPlace(coordinate, toponym, "")
    private lateinit var interestPlaceService: InterestPlaceService


    @Before
    fun setup(): Unit = runBlocking {
        MockitoAnnotations.openMocks(this)
        interestPlaceService = InterestPlaceService(mockRepository)
        interestPlaceService.routeRepository = mockRouteRepository
    }


    @Test
    fun createInterestPlaceByToponym_E1Valido_InterestPlaceCreated(): Unit = runBlocking {
        // Configurar el comportamiento de los mocks
        `when`(mockRepository.createInterestPlace(coordinate, toponym, "")).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )
        `when`(mockRouteRepository.searchInterestPlaceByToponym(toponym)).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )

        assertEquals(interestPlace, interestPlaceService.createInterestPlaceFromToponym(toponym))
        verify(mockRepository).createInterestPlace(coordinate, toponym, "")
        verify(mockRouteRepository).searchInterestPlaceByToponym(toponym)
    }

    @Test(expected = NotSuchPlaceException::class)
    fun  createInterestPlaceByToponym_E1Invalid_InterestPlaceCreated(): Unit = runBlocking {
        doAnswer{ throw NotSuchPlaceException("No existe ese lugar") }
            .`when`(mockRouteRepository).searchInterestPlaceByToponym(anyString())

        interestPlaceService.createInterestPlaceFromToponym("Moztrambique")
    }

    @Test //No funciona
    fun createInterestPlace_E1Valid_InterestPlaceCreated(): Unit = runBlocking{
        `when`(mockRepository.createInterestPlace(coordinate, toponym, "")).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )
        `when`(mockRouteRepository.searchInterestPlaceByCoordinates(coordinate)).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )
        assertEquals(interestPlace, interestPlaceService.createInterestPlaceCoordinates(coordinate))
        verify(mockRepository).createInterestPlace(coordinate, toponym, "")
        verify(mockRouteRepository).searchInterestPlaceByCoordinates(coordinate)
    }

    @Test(expected = NotValidCoordinatesException::class) //No funciona
    fun createInterestPlace_E3Invalid_errorOnCreatingInterestPlace(): Unit = runBlocking {
        doAnswer{ throw NotValidCoordinatesException("Las coordenadas no son válidas") }
            .`when`(mockRouteRepository).searchInterestPlaceByCoordinates(coordinate)

        interestPlaceService.createInterestPlaceCoordinates(coordinate)
    }

}