package acceptanceTests

import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Exceptions.UnableToDeletePlaceException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.ViewModel.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.ViewModel.InterestPlaceService
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate

class InterestPlaceServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var interestPlace: InterestPlace = InterestPlace(Coordinate(-18.665695, 35.529562), "Mozambique", "Moz", false)
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    //Crear valores por defecto para pruebas de create y delete?
    @Test
    fun createInterestPlace_E1Valid_InterestPlaceCreated(): Unit = runBlocking{
        val interestPlaceTest : InterestPlace = interestPlaceService.createInterestPlaceCoordinates(Coordinate(-16.665695, 36.529562)) //Cambiar coordenadas segun los tests
        val res = db.doesInteresPlaceExists(interestPlaceTest.coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceTest.coordinate)
        assertEquals(true, res)
    }

    @Test
    fun createInterestPlace_E2Invalid_errorOnCreatingInterestPlace(): Unit = runBlocking {
        assertThrows<NotValidCoordinatesException>{
            interestPlaceService.createInterestPlaceCoordinates(Coordinate(-1800.665695,35.529562))
        }
    }

    @Test
    fun searchInterestPlace_E1Valid_InterestPlaceFound(): Unit = runBlocking {
        val res: Boolean = interestPlaceService.searchInterestPlace(interestPlace.coordinate)
        assertEquals(true, res)
    }

    @Test
    fun searchInterestPlace_E2Invalid_errorOnSearchingInterestPlace(): Unit = runBlocking {
        assertThrows<NotValidCoordinatesException>{
            interestPlaceService.searchInterestPlace(Coordinate(-300.0,300.0))
        }
    }

    @Test
    fun viewInterestPlaceData_E1Valid_InterestPlaceDataViewed(): Unit = runBlocking {
        val res : Boolean = interestPlaceService.viewInterestPlaceData(interestPlace.coordinate)
        assertEquals(true, res)
    }

    @Test
    fun viewInterestPlaceData_E2Invalid_errorOnViewingInterestPlaceData(): Unit = runBlocking { //Busca en la lista de lugares
        assertThrows<NotSuchPlaceException>{
            interestPlaceService.viewInterestPlaceData(Coordinate(-19.665695,35.529562))
        }
    }
    
    @Test
    fun editInterestPlace_E1Valido_setAliasToAPlaceOfInterest(): Unit = runBlocking {
        val result: Boolean = interestPlaceService.setAlias(interestPlace, newAlias = "Mozambiquinho")
        assertEquals(true, result)
    }

    @Test
    fun editInterestPlace_E2Invalido_errorSetAliasToAPlaceOFInterest(): Unit = runBlocking{
        assertThrows<NotValidAliasException>{
            interestPlaceService.setAlias(interestPlace, newAlias = "@#//")
        }
    }

    @Test
    fun searchInterestPlaceByToponym_E1Valido_InterestPlaceFound(): Unit = runBlocking {
        val res: Boolean = interestPlaceService.searchInterestPlaceByToponym(interestPlace.toponym)
        assertEquals(true, res)
    }

    @Test
    fun searchInterestPlaceByToponym_E2Invalido_errorOnSearchingInterestPlaceByToponym(): Unit = runBlocking {
        assertThrows<NotSuchPlaceException>{
            interestPlaceService.searchInterestPlaceByToponym("Roshar")
        }
    }

    @Test //A la hora de hacer el codigo mirar si miramos una lista generica o logeamos un usuario generico como el de usuario para ver su lista etc.
    fun viewInterestPlaceList_E1Valido_InterestPlaceListViewed(){
        val res = interestPlaceService.viewInterestPlaceList()
        assertEquals(true, res)
    }

    @Test
    fun viewInterestPlaceList_E2Invalido_errorOnViewingInterestPlaceList(){
        //Crear una lista o un usuario con una lista vacia e intentar comprobar que se muestre la lista vacia
    }
    @Test
    fun deleteInterestPlace_E1Valid_InterestPlaceDeleted(): Unit = runBlocking{
        var puntoDelete = Coordinate(39.9333300,-0.1000000 )
        val result = interestPlaceService.deleteInterestPlace(puntoDelete)
        assertEquals(true, result)
    }

    @Test
    fun deleteInterestPlace_E2OInvalid_InterestPlaceDeleted(): Unit = runBlocking{
        var puntoDelete = Coordinate(38.0,-0.0 )
        assertThrows<UnableToDeletePlaceException> {
            interestPlaceService.deleteInterestPlace(puntoDelete)
        }
    }
}