package acceptanceTests

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.api.assertThrows
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.FirebaseRepository
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.InterestPlaceService
import uji.es.intermaps.Model.Repository

class InterestPlaceServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    //private var interestPlace: InterestPlace = InterestPlace(Coordinate(-18.665695, 35.529562), "Mozambique", "Moz", false)
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)

    /*@Test
    fun createInterestPlace_E1Valid_InterestPlaceCreated() {
        val interestPlaceTest: InterestPlace = interestPlaceService.createInterestPlace(Coordinate(-18.665695, 35.529562), "Mozambique", "Moz")
        assertEquals(interestPlace, interestPlaceTest)
        interestPlaceService.deleteInterestPlace(interestPlaceTest.coordinate)
    }

    @Test
    fun createInterestPlace_E2Invalid_errorOnCreatingInterestPlace() {
        assertThrows<NotValidCoordinatesException>{
            interestPlaceService.createInterestPlace(Coordinate(-1800.665695,35.529562), "Mozambique", "Moz")
        }
    }*/

    /*@Test
    fun editInterestPlace_E1Valido_setAliasToAPlaceOfInterest(): Unit  = runBlocking {
        val result: Boolean = interestPlaceService.setAlias(interestPlace, newAlias = "Mozambiquinho")
        assertEquals(true, result)
    }

    @Test
    fun editInterestPlace_E1Invalido_errorSetAliasToAPlaceOFInterest(){
        assertThrows<NotValidAliasException>{
            interestPlaceService.setAlias(interestPlace, newAlias = "@#//")
        }
    }*/
}