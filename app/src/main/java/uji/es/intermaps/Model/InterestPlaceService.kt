package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.*
import uji.es.intermaps.Exceptions.NotValidAliasException


class InterestPlaceService(private val repository: Repository) {
    val customScope = CoroutineScope(Dispatchers.Main)

    fun createInterestPlace(coordinate: GeoPoint, toponym: String, alias: String): InterestPlace{
        return InterestPlace(GeoPoint(0.0,0.0),"","",false)
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