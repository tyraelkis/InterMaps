package uji.es.intermaps.Model

class InterestPlaceService(repository: Repository) {
    fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace{
        return InterestPlace(Coordinate(0.0,0.0),"","",false)
    }

    fun deleteInterestPlace(coordinate: Coordinate): Boolean{
        //elimina el lugar de interés de la base de datos
        return false
    }

    fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean{
        //Cambia o asigna un nuevo alias a un lugar de interés concreto
        return false
    }
}