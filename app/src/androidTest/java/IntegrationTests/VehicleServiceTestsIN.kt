package IntegrationTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.anyDouble
import org.mockito.Mockito.anyString
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import uji.es.intermaps.Exceptions.NotSuchElementException
import uji.es.intermaps.Exceptions.VehicleAlreadyExistsException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.GasolineVehicle
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.Model.VehicleTypes
import uji.es.intermaps.ViewModel.VehicleService

@RunWith(AndroidJUnit4::class)
class VehicleServiceTestsIN {
    @Mock
    var mockRepository: Repository = mock(Repository::class.java)
    private var vehicle: Vehicle = GasolineVehicle("9999GON", VehicleTypes.GASOLINA.type,9.0, false)
    private lateinit var vehicleService: VehicleService

    @Before
    fun setup(): Unit = runBlocking {
        MockitoAnnotations.openMocks(this)
        vehicleService = VehicleService(mockRepository)
    }

    @Test
    fun createVehicle_E1Valid_vehicleIsCreated(): Unit = runBlocking {
        `when`(mockRepository.createVehicle("9999GON", VehicleTypes.GASOLINA.type, 9.0)).thenReturn(vehicle)
        val vehicleTest: Vehicle = vehicleService.createVehicle("9999GON", VehicleTypes.GASOLINA.type, 9.0)
        assertEquals(vehicle.plate, vehicleTest.plate)
        verify(mockRepository).createVehicle("9999GON", VehicleTypes.GASOLINA.type, 9.0)
    }

    @Test(expected = VehicleAlreadyExistsException::class)
    fun createVehicle_E3Invalid_errorOnCreatingVehicle(): Unit = runBlocking {
        doAnswer{ throw VehicleAlreadyExistsException("El vehículo ya existe") }
            .`when`(mockRepository).createVehicle("9999GON", VehicleTypes.GASOLINA.type, 9.0)
        vehicleService.createVehicle("9999GON", VehicleTypes.GASOLINA.type, 9.0)
    }

    @Test
    fun viewVehicleList_E1Valid_vehicleListViewed(): Unit = runBlocking{
        `when`(mockRepository.viewVehicleList()).thenReturn(listOf(vehicle))
        val res = vehicleService.viewVehicleList()
        assertTrue(res.isNotEmpty())
        verify(mockRepository).viewVehicleList()
    }

    @Test
    fun viewVehicleList_E2Valid_emptyVehicleListViewed(): Unit = runBlocking{
        `when`(mockRepository.viewVehicleList()).thenReturn(emptyList())
        val res = vehicleService.viewVehicleList()
        assertTrue(res.isEmpty())
    }

    @Test
    fun deleteVehicle_E1Valid_vehicleDeleted(): Unit = runBlocking{
        `when`(mockRepository.deleteVehicle(anyString())).thenReturn(true)
        assertEquals(true, vehicleService.deleteVehicle("6666NOG"))
        verify(mockRepository).deleteVehicle("6666NOG")
    }

    @Test (expected = NotSuchElementException::class)
    fun deleteVehicle_E2Invalid_vehicleNotDeleted(): Unit = runBlocking{
        doAnswer{ throw NotSuchElementException("No existe ese vehículo") }
            .`when`(mockRepository).deleteVehicle(anyString())
        vehicleService.deleteVehicle("8888COD")
    }

    @Test
    fun viewVehicleData_E1Valid_vehicleDataIsViewed(): Unit = runBlocking{
        `when`(mockRepository.viewVehicleData("9999GON")).thenReturn(vehicle)
        val res = vehicleService.viewVehicleData("9999GON")
        assertEquals(vehicle.plate, res.plate)
        verify(mockRepository).viewVehicleData("9999GON")
    }

    @Test (expected = NotSuchElementException::class)
    fun viewVehicleData_E2Valid_emptyVehicleDataIsViewed(): Unit = runBlocking {
        doAnswer{ throw NotSuchElementException("No existe ese vehículo") }
            .`when`(mockRepository).viewVehicleData(anyString())
        vehicleService.viewVehicleData("8888GON")
    }

    @Test
    fun editVehicleData_E1Valid_vehicleDataEdited(): Unit = runBlocking {
        `when`(mockRepository.editVehicleData("9999GON", VehicleTypes.DIESEL, 11.0)).thenReturn(true)
        val res = vehicleService.editVehicleData("9999GON", VehicleTypes.DIESEL, 11.0)
        assertEquals(true, res)
        verify(mockRepository).editVehicleData("9999GON", VehicleTypes.DIESEL, 11.0)
    }

    @Test (expected = NotSuchElementException::class)
    fun editVehicleData_E2Invalid_errorOnEditingVehicleData(): Unit = runBlocking {
        doAnswer{ throw NotSuchElementException("No existe ese vehículo") }
            .`when`(mockRepository).editVehicleData(anyString(), any(VehicleTypes::class.java), anyDouble())
        vehicleService.editVehicleData("8888COD", VehicleTypes.DIESEL, 11.0)
    }
}