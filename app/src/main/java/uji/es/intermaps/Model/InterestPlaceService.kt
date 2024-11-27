package uji.es.intermaps.Model

import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.*
import uji.es.intermaps.Exceptions.NotValidAliasException

class InterestPlaceService(private val repository: Repository) {
    val customScope = CoroutineScope(Dispatchers.Main)

    suspend fun createInterestPlaceCoordinates(coordinate: GeoPoint): InterestPlace{
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        //Aquí se llama a la API openrouteservice para conseguir el topónimo correspondiente con las coordenadas
        //TODO llamar a la API openrouteservice
        val toponym : String? = null

        return repository.createInterestPlace(coordinate, toponym, null)
    }

    fun deleteInterestPlace(coordinate: GeoPoint): Boolean{
        //elimina el lugar de interés de la base de datos
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

    fun getFavList(callback: (List<InterestPlace>) -> Unit){
        repository.getFavList{ success, favList ->
            if (success){
                callback(favList)
            }else{
                callback(emptyList())
            }
        }
    }

    fun getNoFavList(callback: (List<InterestPlace>) -> Unit){
        repository.getNoFavList{ success, NoFavList ->
            if (success){
                callback(NoFavList)
            }else{
                callback(emptyList())
            }
        }
    }
}