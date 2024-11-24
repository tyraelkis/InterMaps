package uji.es.intermaps.Model

import uji.es.intermaps.Exceptions.AccountAlreadyRegistredException

class FirebaseRepository: Repository{
    var intermapsDB = DataBase

    override fun createUser(user: User): User{ //Mirar si el return puede ser mejor devolviendo boolean y comprobar que coincide el email
        //val result = intermapsDB.auth.createUserWithEmailAndPassword(user.email, user.password)
        //if(result.isSuccessful){
        //    intermapsDB.db.collection("Users").add(mapOf("email" to user.email))
        //}
        return user
    }

    override fun loginUser(user: User): Boolean{
        return false
    }

    override fun viewUserData(user: User): User? { //pasarle el email
        return null
    }

    override fun editUserData(user: User, newPassword:String): Boolean{
        return true
    }

    override fun deleteUser(user: User): Boolean{ //pasarle email
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