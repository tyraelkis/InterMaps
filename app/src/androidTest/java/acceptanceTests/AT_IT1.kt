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
import uji.es.intermaps.Model.DataBase

@RunWith(AndroidJUnit4::class)
class AT_IT1 {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DataBase
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var user: User

    @Before
    fun setUp() {
        auth = Firebase.auth
        db = DataBase
        user = User ("juan@ejemplo.com", "123456")
    }

    @Test
    fun create_E1Valid_userIsCreated() {
        val initialCount = db.getNumberUsers()
        user.createUser(email, password)
        val finalCount = db.getNumberUsers()
        assertEquals( initialCount + 1, finalCount)
    }

    @Test
    fun edit_E1Valid_userDataIsEdited(){
        var newPassword:String = "contraseñaNueva"
        user.editUserData(newPassword)
        assertEquals(user.password, newPassword)
    }

}