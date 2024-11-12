package uji.es.intermaps.Model

class InterestPlace(var coordinate: Coordinate, var toponym: String = "", var alias: String = "", var fav: Boolean = false) {

    fun createInterestPlace(){
        //crea el lugar de interés en la base de datos
    }

    fun deleteInterestPlace(){

    }

    fun setAlias(newAlias : String): Boolean{
        return true
    }
}