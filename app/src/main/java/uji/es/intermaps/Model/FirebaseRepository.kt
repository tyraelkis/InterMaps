package uji.es.intermaps.Model

class FirebaseRepository: Repository{

    override fun createUser(email:String, pswd: String): User{
        return User("a","b")
    }

    override fun loginUser(email:String, pswd: String): Boolean{
        return false
    }

    override fun viewUserData(email: String): User? {
        return null
    }

    override fun editUserData(email: String, newPassword:String): Boolean{
        return true
    }

    override fun deleteUser(email: String): Boolean{
        return false
    }

    override fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean{
        return true
    }

    override fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace {
        return InterestPlace(Coordinate(0.0,0.0), "", "", false)
    }

    override fun deleteInterestPlace(coordinate: Coordinate): Boolean {
        return false
    }

}