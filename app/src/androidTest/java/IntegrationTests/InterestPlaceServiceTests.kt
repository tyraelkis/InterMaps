package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertTrue

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Exceptions.UnableToDeletePlaceException

import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.ViewModel.RouteRepository

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

    @Test
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

    @Test(expected = NotValidCoordinatesException::class)
    fun createInterestPlace_E3Invalid_errorOnCreatingInterestPlace(): Unit = runBlocking {
        doAnswer{ throw NotValidCoordinatesException("Las coordenadas no son válidas") }
            .`when`(mockRouteRepository).searchInterestPlaceByCoordinates(coordinate)

        interestPlaceService.createInterestPlaceCoordinates(coordinate)
    }

    @Test
    fun searchInterestPlaceByCoordinate_E1Valid_InterestPlaceFound(): Unit = runBlocking {
        `when`(mockRouteRepository.searchInterestPlaceByCoordinates(coordinate)).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )
        val foundPlace: InterestPlace = interestPlaceService.searchInterestPlaceByCoordiante(interestPlace.coordinate)
        val resultado : Boolean = foundPlace.toponym.contains(interestPlace.toponym)
        assertEquals(true, resultado)
        verify(mockRouteRepository).searchInterestPlaceByCoordinates(coordinate)
    }

    @Test(expected = NotValidCoordinatesException::class)
    fun searchInterestPlaceByCoordinate_E2Invalid_errorOnSearchingInterestPlace(): Unit = runBlocking {
        val wrongCoordiante = Coordinate(-300.0,300.0)
        doAnswer{ throw NotValidCoordinatesException("Las coordenadas no son válidas") }
            .`when`(mockRouteRepository).searchInterestPlaceByCoordinates(wrongCoordiante)

        interestPlaceService.searchInterestPlaceByCoordiante(wrongCoordiante)
    }

    @Test
    fun viewInterestPlaceData_E1Valid_InterestPlaceDataViewed(): Unit = runBlocking {
        `when`(mockRepository.viewInterestPlaceData(interestPlace.coordinate)).thenReturn(
            InterestPlace(coordinate, toponym, "")
        )
        val foundPlace : InterestPlace = interestPlaceService.viewInterestPlaceData(interestPlace.coordinate)
        val res : Boolean = foundPlace.toponym.contains(interestPlace.toponym)
        assertEquals(true, res)
        verify(mockRepository).viewInterestPlaceData(interestPlace.coordinate)
    }

    @Test(expected = NotSuchPlaceException::class)
    fun viewInterestPlaceData_E2Invalid_errorOnViewingInterestPlaceData(): Unit = runBlocking {
        val wrongCoordiante = (Coordinate(-19.665695, 35.529562))
        doAnswer{ throw NotSuchPlaceException("No existe ese lugar") }
            .`when`(mockRepository).viewInterestPlaceData(wrongCoordiante)
        interestPlaceService.viewInterestPlaceData(wrongCoordiante)
    }

    @Test
    fun editInterestPlace_E1Valido_setAliasToAPlaceOfInterest(): Unit = runBlocking {
        `when`(mockRepository.setAlias(interestPlace, "Mozambiquinho")).thenReturn(true)
        val result: Boolean = interestPlaceService.setAlias(interestPlace, "Mozambiquinho")
        assertEquals(true, result)
        verify(mockRepository).setAlias(interestPlace, "Mozambiquinho")
    }

    @Test(expected = NotValidAliasException::class)
    fun editInterestPlace_E2Invalido_errorSetAliasToAPlaceOFInterest(): Unit = runBlocking{
        doAnswer{ throw NotValidAliasException("El alias no tiene un formato válido.") }
            .`when`(mockRepository).setAlias(interestPlace,"@#//")
        interestPlaceService.setAlias(interestPlace, "@#//")
    }

    @Test
    fun searchInterestPlaceByToponym_E1Valido_InterestPlaceFound(): Unit = runBlocking {
        `when`(mockRouteRepository.searchInterestPlaceByToponym(interestPlace.toponym)).thenReturn(interestPlace)
        val foundPlace: InterestPlace = interestPlaceService.searchInterestPlaceByToponym(interestPlace.toponym)
        val resultado : Boolean = foundPlace.toponym.contains(interestPlace.toponym)
        assertEquals(true, resultado)
        verify(mockRouteRepository).searchInterestPlaceByToponym(interestPlace.toponym)
    }

    @Test(expected = NotSuchPlaceException::class)
    fun searchInterestPlaceByToponym_E2Invalido_errorOnSearchingInterestPlaceByToponym(): Unit = runBlocking {
        doAnswer{ throw NotSuchPlaceException("No existe ese lugar") }
            .`when`(mockRouteRepository).searchInterestPlaceByToponym(anyString())
        interestPlaceService.searchInterestPlaceByToponym("unsitioquenoexisteporfa")
    }

    @Test
    fun viewInterestPlaceList_E1Valido_InterestPlaceListViewed(): Unit = runBlocking{
        `when`(mockRepository.viewInterestPlaceList()).thenReturn(listOf(interestPlace))
        val res = interestPlaceService.viewInterestPlaceList()
        assertTrue(res.isNotEmpty())
        verify(mockRepository).viewInterestPlaceList()
    }

    @Test
    fun viewInterestPlaceList_E2Valido_emptyInterestPlaceListViewed(): Unit = runBlocking{
        `when`(mockRepository.viewInterestPlaceList()).thenReturn(emptyList())
        val res = interestPlaceService.viewInterestPlaceList()
        assertTrue(res.isEmpty())
        verify(mockRepository).viewInterestPlaceList()
    }

    @Test
    fun deleteInterestPlace_E1Valid_InterestPlaceDeleted(): Unit = runBlocking{
        val puntoDelete = Coordinate(39.9333300,-0.1000000)
        `when`(mockRepository.deleteInterestPlace(puntoDelete)).thenReturn(true)
        assertEquals(true, interestPlaceService.deleteInterestPlace(puntoDelete))
        verify(mockRepository).deleteInterestPlace(puntoDelete)
    }

    @Test(expected = UnableToDeletePlaceException::class)
    fun deleteInterestPlace_E2Invalid_InterestPlaceDeleted(): Unit = runBlocking{
        val puntoDelete = Coordinate(38.0,-0.0 )
        doAnswer{ throw UnableToDeletePlaceException("No se puede eliminar ese lugar") }
            .`when`(mockRepository).deleteInterestPlace(puntoDelete)
        interestPlaceService.deleteInterestPlace(puntoDelete)
    }

}