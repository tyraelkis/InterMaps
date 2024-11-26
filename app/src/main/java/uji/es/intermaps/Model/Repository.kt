package uji.es.intermaps.Model

interface Repository {
    suspend fun createUser(email:String, pswd: String): User
    suspend fun loginUser(email:String, pswd: String): Boolean
    suspend fun signOut(): Boolean
    fun viewUserData(email: String) : User?
    fun editUserData(email: String, newPassword:String): Boolean
    fun deleteUser(email: String): Boolean
    fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean
    suspend fun createInterestPlace(coordinate: Coordinate, toponym: String?, alias: String?): InterestPlace
    fun deleteInterestPlace(coordinate: Coordinate): Boolean
}