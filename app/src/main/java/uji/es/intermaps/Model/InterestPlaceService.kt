package uji.es.intermaps.Model

import android.util.Log

class InterestPlaceService(private val repository: Repository) {

    fun createInterestPlace(coordinate: Coordinate, toponym: String, alias: String): InterestPlace{
        return InterestPlace(Coordinate(0.0,0.0),"","",false)
    }

    fun deleteInterestPlace(coordinate: Coordinate): Boolean{
        //elimina el lugar de interés de la base de datos
        return false
    }

    fun setAlias(interestPlace: InterestPlace, newAlias : String): Boolean{
        if (newAlias.length < 2)
            return false;
        for (char in newAlias){
            if (!char.isLetter() && char != ' '){
                return false;
            }
        }
        var result = false
        repository.setAlias(interestPlace, newAlias) {success ->
            if (success){
                interestPlace.alias = newAlias;
                result = true
                Log.i("Funciona", "el metodo funciona")
            }else{
                Log.i("No Funciona", "el metodo no funciona")
            }
        }
        return result
    }
}