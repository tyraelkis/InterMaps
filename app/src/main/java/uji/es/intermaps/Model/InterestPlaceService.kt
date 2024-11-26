package uji.es.intermaps.Model

import uji.es.intermaps.Exceptions.NotValidCoordinatesException

class InterestPlaceService(var repository: Repository) {
    suspend fun createInterestPlaceCoordinates(coordinate: Coordinate): InterestPlace{
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        //Aquí se llama a la API openrouteservice para conseguir el topónimo correspondiente con las coordenadas
        //TODO llamar a la API openrouteservice
        val toponym : String? = null

        return repository.createInterestPlace(coordinate, toponym, null)
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