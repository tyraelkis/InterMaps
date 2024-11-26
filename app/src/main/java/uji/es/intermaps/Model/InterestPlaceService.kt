package uji.es.intermaps.Model

import android.util.Log
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.*


class InterestPlaceService(private val repository: Repository) {
    val customScope = CoroutineScope(Dispatchers.Main)

    fun createInterestPlace(coordinate: GeoPoint, toponym: String, alias: String): InterestPlace{
        return InterestPlace(GeoPoint(0.0,0.0),"","",false)
    }

    fun deleteInterestPlace(coordinate: GeoPoint): Boolean{
        //elimina el lugar de interés de la base de datos
        return false
    }

    fun setAlias(interestPlace: InterestPlace, newAlias : String, callback: (Boolean) -> Unit){
        if (newAlias.length < 2 || interestPlace.alias.equals(newAlias))
            callback(false)
        for (char in newAlias){
            if (!char.isLetter() && char != ' '){
                callback(true)
            }
        }

        repository.setAlias(interestPlace, newAlias) {success ->
            if (success){
                interestPlace.alias = newAlias;
                callback(true)
            }
        }
    }

    fun getFavList(callback: (List<InterestPlace>) -> Unit){
        var result = false;
        var favListFinal = mutableListOf<InterestPlace>()
        repository.getFavList{ success, favList ->
            if (success){
                callback(favList)
            }else{
                callback(emptyList())
            }
        }
    }

    fun getNoFavList(callback: (List<InterestPlace>) -> Unit){

    }
}