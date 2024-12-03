package uji.es.intermaps.ViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Interfaces.ORSRepository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.RetrofitConfig

class RouteRepository : ORSRepository {
    val apiKey = "5b3ce3597851110001cf6248d49685f8848445039a3bcb7f0da42f23"

    override suspend fun searchInterestPlaceByCoordinates(coordinate: Coordinate): InterestPlace {
        if (coordinate.latitude < -90 || coordinate.latitude > 90 || coordinate.longitude < -180 || coordinate.longitude > 180){
            throw NotValidCoordinatesException("Las coordenadas no son válidas")
        }
        val openRouteService = RetrofitConfig.createRetrofitOpenRouteService()
        var toponym = ""

        //Llamada a la API
        val response = withContext(Dispatchers.IO) {
            openRouteService.getToponym(
                apiKey,
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
        return InterestPlace(coordinate, toponym, "", false)
    }

    override suspend fun searchInterestPlaceByToponym(toponym: String): InterestPlace {
        TODO("Not yet implemented")
    }
}