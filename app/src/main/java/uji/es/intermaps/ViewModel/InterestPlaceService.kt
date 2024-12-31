package uji.es.intermaps.ViewModel

import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Exceptions.UnableToDeletePlaceException
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.CachePrecioLuz
import uji.es.intermaps.Model.ConsultorPreciLuz
import uji.es.intermaps.Model.Coordinate

open class InterestPlaceService(private val repository: Repository) {
    var routeRepository = RouteRepository(CachePrecioLuz(ConsultorPreciLuz()))

    suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace {
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        if (alias.length > 30) {
            throw NotValidAliasException("El alias tiene un máximo de 30 caracteres.")
        }
        val aliasRegex = "^[A-Za-z0-9\\s]*$".toRegex()
        if (!alias.matches(aliasRegex)) {
            throw NotValidAliasException("El alias no tiene un formato válido.")
        }

        return repository.createInterestPlace(coordinate, toponym, alias)
    }

    suspend fun createInterestPlaceCoordinates(coordinate: Coordinate): InterestPlace {
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        //Aquí se llama a la API openrouteservice para conseguir el topónimo correspondiente con las coordenadas
        //Clase que realizará las llamadas con sus métodos
        val interestPlaceCreated = routeRepository.searchInterestPlaceByCoordinates(coordinate)

        return repository.createInterestPlace(interestPlaceCreated.coordinate, interestPlaceCreated.toponym, "")
    }

    suspend fun createInterestPlaceFromToponym(toponym: String): InterestPlace {
        if (toponym.isBlank()) {
            throw NotSuchPlaceException()
        }

        val coordinate = searchInterestPlaceByToponym(toponym).coordinate
        if (coordinate == Coordinate(0.0, 0.0)) {
            throw NotSuchPlaceException()
        }
        val result = repository.createInterestPlace(coordinate, toponym, "")
        // Crear el lugar de interés
        return result
    }

    suspend fun deleteInterestPlace(coordinate: Coordinate): Boolean{
        if (repository.deleteInterestPlace(coordinate)){
            return true
        }
        else{
            throw UnableToDeletePlaceException()
        }
    }

    suspend fun searchInterestPlaceByCoordiante(coordinate: Coordinate) : InterestPlace{
        return routeRepository.searchInterestPlaceByCoordinates(coordinate)
    }

    suspend fun viewInterestPlaceData(coordinate: Coordinate): InterestPlace{
       return repository.viewInterestPlaceData(coordinate)
    }

    suspend fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean{
        if (newAlias.length < 2 || interestPlace.alias.equals(newAlias))
            throw NotValidAliasException()
        for (char in newAlias){
            if (!char.isLetter() && char != ' '){
                throw NotValidAliasException()
            }
        }
        if (!repository.setAlias(interestPlace, newAlias)){
            throw NotValidAliasException()
        }
        interestPlace.alias = newAlias
        return true
    }

    suspend fun searchInterestPlaceByToponym(toponym: String) : InterestPlace{
        val result = routeRepository.searchInterestPlaceByToponym(toponym)
        return result
    }

    suspend fun viewInterestPlaceList(): List<InterestPlace> {
        return repository.viewInterestPlaceList()
    }


    suspend fun getInterestPlaceByToponym(toponym: String): InterestPlace {
        val returnPlace = repository.getInterestPlaceByToponym(toponym)
        return returnPlace
    }

    suspend fun setFavInterestPlace(coordinate: Coordinate) : Boolean {
        return repository.setFavInterestPlace(coordinate)
    }

    suspend fun deleteFavInterestPlace(coordinate: Coordinate) : Boolean {
        return repository.deleteFavInterestPlace(coordinate)
    }

}