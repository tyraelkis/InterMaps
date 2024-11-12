package uji.es.intermaps.Model

class User (var email: String, var password: String){

    fun createUser(){
        //crea el usuario en la base de datos
    }

    fun login() : Boolean{
        return false
    }

    fun editUserData(newPassword:String){

    }

    fun viewUserData(){

    }

    fun deleteUser(){

    }
}

