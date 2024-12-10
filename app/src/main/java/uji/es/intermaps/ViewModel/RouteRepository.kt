package uji.es.intermaps.ViewModel

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uji.es.intermaps.Exceptions.NotSuchPlaceException
import uji.es.intermaps.Exceptions.NotValidCoordinatesException
import uji.es.intermaps.Interfaces.ORSRepository
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.InterestPlace
import uji.es.intermaps.Model.RetrofitConfig
import uji.es.intermaps.Model.Route

open class RouteRepository : ORSRepository {
    private val apiKey = "5b3ce3597851110001cf6248d49685f8848445039a3bcb7f0da42f23"

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
        val openRouteService = RetrofitConfig.createRetrofitOpenRouteService()
        val coordinate: Coordinate

        try {
            // Llamada a la API para obtener las coordenadas del topónimo
            val response = openRouteService.getCoordinatesFromToponym(
                apiKey,
                toponym
            )
            val respuesta = response.features
            if (respuesta.isNotEmpty()) {
                val feature = respuesta[0]
                val lon = feature.geometry.coordinates[0]
                val lat = feature.geometry.coordinates[1]

                // Validamos las coordenadas
                if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                    throw NotValidCoordinatesException("Las coordenadas no son válidas")
                }

                coordinate = Coordinate(lat,lon)
                return InterestPlace(coordinate, toponym, "", false)
            }

        } catch (e: Exception) {
            // Manejo de excepciones
            Log.e("Coordenadas", "Error: ${e.message}")
        }

        throw NotSuchPlaceException("Error en la llamada a la API para obtener las coordenadas")
    }

    override suspend fun calculateRoute(origin: Coordinate, destination: Coordinate): Route {
        TODO("Not yet implemented")
    }
}