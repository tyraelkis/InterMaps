package uji.es.intermaps.Model

class FirebaseRepository: Repository{

    override fun createUser(email:String, pswd: String){
        //añadir el usuario en la base de datos
    }

    override fun loginUser(email:String, pswd: String){

    }

    override fun viewUserData(email: String): User? {
        return null
    }

    override fun editUserData(email: String, newPassword:String): Boolean{
        return true
    }

    override fun deleteUser(email: String){

    }

    override fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean{
        return true
    }

}