package uji.es.intermaps.Model

interface Repository {
    fun createUser(email:String, pswd: String)
    fun loginUser(email:String, pswd: String)
    fun viewUserData(email: String): User?
    fun editUserData(email: String, newPassword:String): Boolean
    fun deleteUser(email: String)
    fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean
}