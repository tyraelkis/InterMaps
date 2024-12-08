package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Exceptions.VehicleAlreadyExistsException
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.User
import uji.es.intermaps.Model.Vehicle
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.ViewModel.UserService
import uji.es.intermaps.ViewModel.VehicleService


@RunWith(AndroidJUnit4::class)
class VehicleTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var vehicle: Vehicle = Vehicle("9999GON", "Gasolina")
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
        val vehicleTest: Vehicle = vehicleService.createVehicle("6666NOG", "Gasolina", 9.0)
        val res = db.doesVehicleExist(vehicleTest.plate)
        vehicleService.deleteVehicle(vehicleTest.plate)
        assertEquals(true, res)
    }

    @Test (expected = VehicleAlreadyExistsException::class)
    fun createVehicle_E2Invalid_errorOnCreatingVehicle(): Unit = runBlocking {
        // El vehiculo  ya esta en la BD
        vehicleService.createVehicle("9999GON", "Gasolina", 9.0)
    }

}