package acceptanceTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import uji.es.intermaps.Model.User
import org.junit.Assert.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.assertThrows
import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase
import uji.es.intermaps.Model.InterestPlace

@RunWith(AndroidJUnit4::class)
class AT_IT1 {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DataBase
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var user: User

    @BeforeAll
    fun setUp() {
        auth = Firebase.auth
        db = DataBase
        email = "prueba@uji.es"
        password = "12345"
        user = User(email, password)
    }

    @Test
    fun create_E1Valid_userIsCreated() {
        val initialCount = db.getNumberUsers()
        user.createUser()
        val finalCount = db.getNumberUsers()
        assertEquals(initialCount + 1, finalCount)
        user.deleteUser(email, password)
    }

    @Test
    fun create_E2Invalid_AccountAlreadyRegistredException() { //Revisar la prueba. Además mirar si asi vale o hay que usar AfterEach para revertir las pruebas
        user.createUser()
        try {
            user.createUser()
            fail("Ya existe el usuario")
        }catch (e: AccountAlreadyRegistredException){
            assertEquals("Ya existe el usuario", e.message)
        }
        user.deleteUser(email, password)
    }

    @Test
    fun edit_E1Valid_userDataEdited() {
        val newPassword = "contraseñaNueva"
        user.editUserData(email, newPassword)
        assertEquals(password, newPassword)
    }

    @Test
    fun view_E1Valid_userDataViewed() {
        val expectedEmail = "juan@ejemplo.com"
        val expectedPassword = "123456"
        user.viewUserData(email)
        assertEquals(expectedEmail, email)
        assertEquals(expectedPassword, password)
    }

    @Test
    fun delete_E1Valid_userIsDeleted() {
        val initialCount = db.getNumberUsers()
        user.deleteUser(email, password)
        val finalCount = db.getNumberUsers()
        assertEquals(initialCount - 1, finalCount)
    }

    @Test
    fun edit_E1Valido_setAliasToAPlaceOfInterest() {
        val coordinates = Coordinate(-18.665695, 35.529562)
        val newAlias = "Mozambiquinho"
        val placeOfInterest = InterestPlace(coordinates, "Mozambique", "Moz")
        assertEquals(true, placeOfInterest.setAlias(newAlias))
    }

}