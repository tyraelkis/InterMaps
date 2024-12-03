package uji.es.intermaps.ViewModel

import android.util.Log
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await
import uji.es.intermaps.Exceptions.NotValidAliasException
import uji.es.intermaps.Interfaces.ORSRepository
import uji.es.intermaps.Exceptions.UnableToDeletePlaceException
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Interfaces.Repository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.DataBase.db
import uji.es.intermaps.Model.RetrofitConfig
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class InterestPlaceService(private val repository: Repository) {
    private val routeRepository = RouteRepository()

    suspend fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace {
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        if (alias.length > 30) {
            throw IllegalArgumentException("El alias tiene un máximo de 30 caracteres.")
        }
        val aliasRegex = "^[A-Za-z0-9]+$".toRegex()
        if (!alias.matches(aliasRegex)) {
            throw IllegalArgumentException("El alias no tiene un formato válido.")
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

    suspend fun deleteInterestPlace(coordinate: Coordinate): Boolean{
        if (repository.deleteInterestPlace(coordinate)){
            return true
        }
        else{
            throw UnableToDeletePlaceException()
        }
        return false
    }

    suspend fun searchInterestPlaceByCoordiante(coordinate: Coordinate) : InterestPlace{
        return routeRepository.searchInterestPlaceByCoordinates(coordinate)
    }

    fun viewInterestPlaceData(coordinate: Coordinate): Boolean{
        return false
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
            return false
        }
        interestPlace.alias = newAlias
        return true
    }

    suspend fun searchInterestPlaceByToponym(toponym: String) : InterestPlace{
        return routeRepository.searchInterestPlaceByToponym(toponym)
    }

    suspend fun viewInterestPlaceList(email: String? = null): List<InterestPlace> {
        return repository.viewInterestPlaceList(email)
    }


    suspend fun getInterestPlaceByToponym(toponym: String, callback: (List<InterestPlace>) -> Unit) {
        repository.getInterestPlaceByToponym(toponym) { success, interestPlace ->
            if (success) {
                callback(interestPlace)
            } else {
                callback(emptyList())
            }

        }
    }

    suspend fun createInterestPlaceFromToponym(toponym: String): InterestPlace {
        if (toponym.isBlank()) {
            throw IllegalArgumentException("El topónimo no puede estar vacío")
        }

        val coordinate = searchInterestPlaceByToponym(toponym).coordinate
        if (coordinate == Coordinate(0.0, 0.0)) {
            throw Exception("No se pudieron obtener las coordenadas para el topónimo proporcionado")
        }
        // Crear el lugar de interés
        return repository.createInterestPlace(coordinate, toponym, "")
    }

}