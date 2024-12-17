package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Exceptions.NotSuchElementException
import uji.es.intermaps.Exceptions.VehicleAlreadyExistsException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.GasolineVehicle
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.Model.VehicleTypes
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.UserService
import uji.es.intermaps.ViewModel.VehicleService


@RunWith(AndroidJUnit4::class)
class VehicleServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var vehicle: Vehicle = GasolineVehicle("9999GON", VehicleTypes.GASOLINA.type,9.0, false)
    private var vehicleService: VehicleService = VehicleService(repository)
    private var email: String = "emaildeprueba@gmail.com" //Usuario con lista de lugares. Hay que añadirle un lugar
    private var emailEmpty: String = "emaildepruebaempty@gmail.com" //Usuario sin lista de lugares
    val userService = UserService(FirebaseRepository())
    private val userTest: User = User("emaildeprueba@gmail.com", "123456BB")

    @Before
    fun setup(): Unit = runBlocking {
        userService.login(userTest.email, userTest.pswd)
    }

    @After
    fun tearDown(): Unit = runBlocking {
        userService.signOut()
    }

    @Test
    fun createVehicle_E1Valid_vehicleIsCreated(): Unit = runBlocking {
        val vehicleTest: Vehicle = vehicleService.createVehicle("6666NOG", VehicleTypes.GASOLINA.type, 9.0)
        val res = db.doesVehicleExist(vehicleTest.plate)
        vehicleService.deleteVehicle(vehicleTest.plate)
        assertEquals(true, res)
    }

    @Test (expected = VehicleAlreadyExistsException::class)
    fun createVehicle_E3Invalid_errorOnCreatingVehicle(): Unit = runBlocking {
        // El vehiculo  ya esta en la BD
        vehicleService.createVehicle("9999GON", VehicleTypes.GASOLINA.type, 9.0)
    }

    @Test
    fun viewVehicleList_E1Valid_vehicleListViewed(): Unit = runBlocking{
        val res = vehicleService.viewVehicleList()
        assertTrue(res.isNotEmpty())
    }

    @Test
    fun viewVehicleList_E2Valid_emptyVehicleListViewed(): Unit = runBlocking{
        userService.signOut()
        userService.login(emailEmpty, "123456BB")
        val res = vehicleService.viewVehicleList()
        userService.login(emailEmpty, "123456BB")
        assertTrue(res.isEmpty())
    }

    @Test
    fun deleteVehicle_E1Valid_vehicleDeleted(): Unit = runBlocking{
        vehicleService.createVehicle("6666NOG", VehicleTypes.GASOLINA.type, 9.0)
        assertEquals(true, vehicleService.deleteVehicle("6666NOG"))
    }

    @Test (expected = NotSuchElementException::class)
    fun deleteVehicle_E2Invalid_vehicleNotDeleted(): Unit = runBlocking{
        vehicleService.deleteVehicle("8888COD")
    }

}