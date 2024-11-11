package uji.es.intermaps.Model

class InterestPlace(var coordinate: Coordinate, var toponym: String = "", var alias: String = "", var fav: Boolean = false) {



    fun setAlias(newAlias : String): Boolean{
        return true
    }
}