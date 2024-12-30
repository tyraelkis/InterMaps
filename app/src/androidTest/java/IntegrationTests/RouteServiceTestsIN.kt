package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import uji.es.intermaps.APIParsers.RouteFeature
import uji.es.intermaps.APIParsers.RouteSummary
import uji.es.intermaps.Exceptions.NotSuchElementException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.Route
import uji.es.intermaps.Model.RouteTypes
import uji.es.intermaps.Model.TransportMethods
import uji.es.intermaps.Model.VehicleTypes
import uji.es.intermaps.ViewModel.RouteRepository
import uji.es.intermaps.ViewModel.RouteService

@RunWith(AndroidJUnit4::class)
class RouteServiceTestsIN {
    @Mock
    private var mockRepository: Repository = mock(Repository::class.java)
    private var mockRouteRepository: RouteRepository = mock(RouteRepository::class.java)
    private lateinit var routeService: RouteService

    private val route = Route("","", emptyList(), 0.0, 0.0.toString(),0.0,  RouteTypes.RAPIDA, false, TransportMethods.VEHICULO, "")

    @Before
    fun setup():Unit = runBlocking {
        routeService = RouteService(mockRepository)
        routeService.routeRepository = mockRouteRepository
    }

    @Test
    fun createRoute_E1Valid_routeIsCreated():Unit = runBlocking {
        // Mockear la creación de la ruta
        val mockedRoute = Route(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            route = emptyList(),
            distance = 0.0,
            duration = 0.0.toString(),
            cost = 0.0,
            routeType = RouteTypes.RAPIDA,
            fav = false,
            vehiclePlate = "9999GON",
        )

        val mockedCall = RouteFeature(
            geometry = "",
            summary =
                RouteSummary(
                    distance = 0.0,
                    duration = 0.0
            ),
        )

        doReturn(
            InterestPlace(
                Coordinate(39.888399, -0.085748),
                toponym = "Burriana",""
            )
        ).`when`(mockRepository).getInterestPlaceByToponym("Burriana")

        doReturn(
            InterestPlace(
                Coordinate(39.987142, -0.037787),
                toponym = "Castellón de la Plana",""
            )
        ).`when`(mockRepository).getInterestPlaceByToponym("Castellón de la Plana")

        doReturn(mockedCall).`when`(mockRouteRepository).calculateRoute("-0.085748,39.888399", "-0.037787,39.987142",TransportMethods.VEHICULO,RouteTypes.RAPIDA)


        doReturn(mockedRoute).`when`(mockRouteRepository).createRoute(
            origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            routeType = RouteTypes.RAPIDA,
            vehiclePlate = "9999GON",
            route = mockedCall
        )

        val routeTest = routeService.createRoute("Burriana", "Castellón de la Plana", TransportMethods.VEHICULO,RouteTypes.RAPIDA, "9999GON").second
        // Comprobamos que la ruta fue creada correctamente
        assertEquals(mockedRoute, routeTest)
        verify(mockRepository).getInterestPlaceByToponym("Burriana")
        verify(mockRepository).getInterestPlaceByToponym("Castellón de la Plana")
        verify(mockRouteRepository).calculateRoute("-0.085748,39.888399", "-0.037787,39.987142", transportMethod = TransportMethods.VEHICULO,RouteTypes.RAPIDA)
        verify(mockRouteRepository).createRoute(origin = "Burriana",
            destination = "Castellón de la Plana",
            transportMethod = TransportMethods.VEHICULO,
            routeType = RouteTypes.RAPIDA,
            vehiclePlate = "9999GON",
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
            geometry = "",
            summary =  RouteSummary(
                distance = 1200.0,
                duration = 1100.0
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

    @Test
    fun deleteRoute_E2Valid_routeDeleted(): Unit = runBlocking {
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

        `when`(
            mockRepository.deleteRoute(mockedRoute)
        ).thenReturn(true)

        val result = routeService.deleteRoute(
            mockedRoute
        )

        verify(mockRepository).deleteRoute(
            mockedRoute
        )

        assertEquals(true, result)
    }

    @Test
    fun setFavRoute_E1Valid_routeIsFav(): Unit = runBlocking {
        `when`(mockRepository.setFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")).thenReturn(true)
        val result: Boolean = routeService.setFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        Assert.assertEquals(true, result)
        verify(mockRepository).setFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
    }

    @Test (expected = NotSuchElementException::class)
    fun setFavRoute_E3Invalid_errorOnSettingFavRoute(): Unit = runBlocking {
        doAnswer{ throw NotSuchElementException("No existe esa ruta") }
            .`when`(mockRepository).setFavRoute("Burriana", "Castellón de la Plana", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        routeService.setFavRoute("Burriana", "Castellón de la Plana", TransportMethods.APIE, RouteTypes.RAPIDA, "")
    }

    @Test
    fun deleteFavRoute_E1Valid_routeDeleteAsFavourite(): Unit = runBlocking {
        `when`(mockRepository.deleteFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")).thenReturn(true)
        val result: Boolean = routeService.deleteFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        Assert.assertEquals(true, result)
        verify(mockRepository).deleteFavRoute("Galicia", "Alicante", TransportMethods.APIE, RouteTypes.RAPIDA, "")
    }

    @Test (expected = NotSuchElementException::class)
    fun deleteFavRoute_E3Invalid_errorOnDeletingFavRoute(): Unit = runBlocking {
        doAnswer{ throw NotSuchElementException("No existe esa ruta") }
            .`when`(mockRepository).deleteFavRoute("Burriana", "Castellón de la Plana", TransportMethods.APIE, RouteTypes.RAPIDA, "")
        routeService.deleteFavRoute("Burriana", "Castellón de la Plana", TransportMethods.APIE, RouteTypes.RAPIDA, "")
    }
}