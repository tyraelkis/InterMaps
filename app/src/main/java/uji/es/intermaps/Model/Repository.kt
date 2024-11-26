package uji.es.intermaps.Model

interface Repository {
    suspend fun createUser(email:String, pswd: String): User
    suspend fun loginUser(email:String, pswd: String): Boolean
    suspend fun signOut(): Boolean
    //fun editUserData(email: String, newPassword:String): Boolean
    suspend fun viewUserData(email: String) : User?
    //suspend fun editUserEmail(newEmail:String): Boolean
    fun editUserPassword(newPassword:String): Boolean
    fun deleteUser(email: String, password: String): Boolean
    fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean
    fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace
    fun deleteInterestPlace(coordinate: Coordinate): Boolean
    //suspend fun createUser(email: String, pswd: String, vehicle: String): User
}