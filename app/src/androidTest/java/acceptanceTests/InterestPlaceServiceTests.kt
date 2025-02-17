package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
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
import uji.es.intermaps.Model.User
import uji.es.intermaps.ViewModel.UserService

@RunWith(AndroidJUnit4::class)
class InterestPlaceServiceTests {
    private var db: DataBase = DataBase
    private var repository: Repository = FirebaseRepository()
    private var interestPlace: InterestPlace = InterestPlace(Coordinate(-18.665695, 35.529562), "Mozambique", "Moz", false)
    private var interestPlaceService: InterestPlaceService = InterestPlaceService(repository)
    private var email: String = "emaildeprueba@gmail.com"
    private var emailEmpty: String = "emaildepruebaempty@gmail.com"
    private val userService = UserService(FirebaseRepository())
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
    fun createInterestPlace_E1Valid_InterestPlaceCreated(): Unit = runBlocking{
        val puntoDelete = Coordinate(-16.665695, 36.529562)
        val interestPlaceTest: InterestPlace = interestPlaceService.createInterestPlaceCoordinates(puntoDelete)
        val res = db.doesInteresPlaceExists(interestPlaceTest.coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceTest.coordinate)
        assertEquals(true, res)
    }

    @Test(expected = NotValidCoordinatesException::class)
    fun createInterestPlace_E3Invalid_errorOnCreatingInterestPlace(): Unit = runBlocking {
        interestPlaceService.createInterestPlaceCoordinates(Coordinate(-1800.665695,35.529562))
    }

    @Test
    fun searchInterestPlaceByCoordinate_E1Valid_InterestPlaceFound(): Unit = runBlocking {
        val foundPlace: InterestPlace = interestPlaceService.searchInterestPlaceByCoordiante(interestPlace.coordinate)
        val resultado : Boolean = foundPlace.toponym.contains(interestPlace.toponym)
        assertEquals(true, resultado)
    }

    @Test(expected = NotValidCoordinatesException::class)
    fun searchInterestPlaceByCoordinate_E2Invalid_errorOnSearchingInterestPlace(): Unit = runBlocking {
        interestPlaceService.searchInterestPlaceByCoordiante(Coordinate(-300.0,300.0))
    }

    @Test
    fun viewInterestPlaceData_E1Valid_InterestPlaceDataViewed(): Unit = runBlocking {
        val foundPlace : InterestPlace = interestPlaceService.viewInterestPlaceData(interestPlace.coordinate)
        val res : Boolean = foundPlace.toponym.contains(interestPlace.toponym)
        assertEquals(true, res)
    }

    @Test(expected = NotSuchPlaceException::class)
    fun viewInterestPlaceData_E2Invalid_errorOnViewingInterestPlaceData(): Unit = runBlocking {
        interestPlaceService.viewInterestPlaceData((Coordinate(-19.665695, 35.529562)))
    }
    
    @Test
    fun setAlias_E1Valido_aliasChangedToAPlaceOfInterest(): Unit = runBlocking {
        val result: Boolean = interestPlaceService.setAlias(interestPlace, newAlias = "Mozambiquinho")
        assertEquals(true, result)
    }

    @Test(expected = NotValidAliasException::class)
    fun setAlias_E2Invalido_errorChangingAliasToAPlaceOfInterest(): Unit = runBlocking{
        interestPlaceService.setAlias(interestPlace, newAlias = "@#//")
    }

    @Test
    fun searchInterestPlaceByToponym_E1Valido_InterestPlaceFound(): Unit = runBlocking {
        val foundPlace: InterestPlace = interestPlaceService.searchInterestPlaceByToponym(interestPlace.toponym)
        val resultado : Boolean = foundPlace.toponym.contains(interestPlace.toponym)
        assertEquals(true, resultado)
    }

    @Test(expected = NotSuchPlaceException::class)
    fun searchInterestPlaceByToponym_E2Invalido_errorOnSearchingInterestPlaceByToponym(): Unit = runBlocking {
        interestPlaceService.searchInterestPlaceByToponym("unsitioquenoexisteporfa")
    }

    @Test
    fun viewInterestPlaceList_E1Valido_InterestPlaceListViewed(): Unit = runBlocking{
        val res = interestPlaceService.viewInterestPlaceList()
        assertTrue(res.isNotEmpty())
    }

    @Test
    fun viewInterestPlaceList_E2Valido_emptyInterestPlaceListViewed(): Unit = runBlocking{
        userService.signOut()
        userService.login(emailEmpty, "123456BB")
        val res = interestPlaceService.viewInterestPlaceList()
        userService.login(email, "123456BB")
        assertTrue(res.isEmpty())
    }
    @Test
    fun deleteInterestPlace_E1Valid_InterestPlaceDeleted(): Unit = runBlocking{
        val puntoDelete = Coordinate(39.9333300,-0.1000000)
        interestPlaceService.createInterestPlaceCoordinates(puntoDelete)
        assertEquals(true, interestPlaceService.deleteInterestPlace(puntoDelete))
    }

    @Test(expected = UnableToDeletePlaceException::class)
    fun deleteInterestPlace_E2Invalid_InterestPlaceDeleted(): Unit = runBlocking{
        val puntoDelete = Coordinate(38.0,-0.0 )
        interestPlaceService.deleteInterestPlace(puntoDelete)
        }

    @Test
    fun  createInterestPlaceByToponym_E1Valid_InterestPlaceCreated(): Unit = runBlocking {
        val interestPlaceTest : InterestPlace = interestPlaceService.createInterestPlaceFromToponym("Mozambique")
        val res = db.doesInteresPlaceExists(interestPlaceTest.coordinate)
        interestPlaceService.deleteInterestPlace(interestPlaceTest.coordinate)
        assertEquals(true, res)
    }

    @Test(expected = NotSuchPlaceException::class)
    fun  createInterestPlaceByToponym_E1Invalid_InterestPlaceCreated(): Unit = runBlocking {
        interestPlaceService.createInterestPlaceFromToponym("Moztrambique")
    }

    @Test
    fun setFavouriteInterestPlace_E1Valid_InterestPlaceSetAsFavourite(): Unit = runBlocking {
        val res = interestPlaceService.setFavInterestPlace(interestPlace.coordinate)
        assertEquals(true,res)
    }

    @Test(expected = NotSuchPlaceException::class)
        fun setFavouriteInterestPlace_E3Invalid_errorOnSettingFavInterestPlace(): Unit = runBlocking {
            interestPlaceService.setFavInterestPlace(Coordinate(38.0,-0.0))
        }

    @Test
    fun deleteFavInterestPlace_E1Valid_InterestPlaceDeletedAsFavourite(): Unit = runBlocking {
        val res = interestPlaceService.deleteFavInterestPlace(interestPlace.coordinate)
        assertEquals(true,res)
    }

    @Test (expected = NotSuchPlaceException::class)
    fun deleteFavInterestPlace_E3Invalid_errorOnDeletingFavInterestPlace(): Unit = runBlocking {
        interestPlaceService.deleteFavInterestPlace(Coordinate(38.0,-0.0))
    }
}
