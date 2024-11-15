package uji.es.intermaps.Model

interface Repository {
    fun createUser(email:String, pswd: String)
    fun loginUser(email:String, pswd: String)
}