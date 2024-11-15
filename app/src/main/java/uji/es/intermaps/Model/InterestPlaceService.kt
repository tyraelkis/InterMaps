package uji.es.intermaps.Model

class InterestPlaceService(repository: Repository) {
    fun createInterestPlace(){
        //crea el lugar de interés en la base de datos
    }

    fun deleteInterestPlace(){
        //elimina el lugar de interés de la base de datos
    }

    fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean{
        //Cambia o asigna un nuevo alias a un lugar de interés concreto
        return true
    }
}