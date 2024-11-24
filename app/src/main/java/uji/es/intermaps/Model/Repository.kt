package uji.es.intermaps.Model

interface Repository {
    fun createUser(user: User): User
    fun loginUser(user: User): Boolean
    fun viewUserData(user: User): User?
    fun editUserData(user: User, newPassword:String): Boolean
    fun deleteUser(user: User): Boolean
    fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean
    fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace
    fun deleteInterestPlace(coordinate: Coordinate): Boolean
}