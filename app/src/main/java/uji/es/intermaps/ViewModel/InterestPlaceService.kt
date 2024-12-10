package uji.es.intermaps.ViewModel

import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import kotlinx.coroutines.*
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Exceptions.UnableToDeletePlaceException
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.RetrofitConfig

open class InterestPlaceService(private val repository: Repository) {
    var routeRepository = RouteRepository()

    suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace {
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        if (alias.length > 30) {
            throw NotValidAliasException("El alias tiene un máximo de 30 caracteres.")
        }
        val aliasRegex = "^[A-Za-z0-9\\s]+$".toRegex()
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
        val openRouteService = RetrofitConfig.createRetrofitOpenRouteService()
        var toponym = ""

        //Llamada a la API
        val response = withContext(Dispatchers.IO) {
            openRouteService.getToponym(
                "5b3ce3597851110001cf6248d49685f8848445039a3bcb7f0da42f23",
                coordinate.longitude,
                coordinate.latitude
            ).execute()
        }
        if (response.isSuccessful) {
            response.body()?.let { ORSResponse ->
                val respuesta = ORSResponse.features
                if (respuesta.isNotEmpty()) {
                    toponym = respuesta[0].properties.label
                }
            }
        } else {
            throw Exception("Error en la llamada a la API")
        }

        return repository.createInterestPlace(coordinate, toponym, "")
    }

    suspend fun createInterestPlaceFromToponym(toponym: String): InterestPlace {
        if (toponym.isBlank()) {
            throw NotSuchPlaceException()
        }

        val coordinate = searchInterestPlaceByToponym(toponym).coordinate
        if (coordinate == Coordinate(0.0, 0.0)) {
            throw NotSuchPlaceException()
        }
        var result = repository.createInterestPlace(coordinate, toponym, "")
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
        var result = routeRepository.searchInterestPlaceByToponym(toponym)
        return result
    }

    suspend fun viewInterestPlaceList(): List<InterestPlace> {
        return repository.viewInterestPlaceList()
    }


    suspend fun getInterestPlaceByToponym(toponym: String): InterestPlace {
        val returnPlace = repository.getInterestPlaceByToponym(toponym)
        return returnPlace
    }

}