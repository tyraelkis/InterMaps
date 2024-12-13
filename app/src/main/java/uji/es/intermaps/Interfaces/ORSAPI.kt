package uji.es.intermaps.Interfaces

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

import retrofit2.http.Query
import uji.es.intermaps.APIParsers.CoordToToponymORSAPIResponse
import uji.es.intermaps.APIParsers.ToponymToCoordORSAPIResponse
import uji.es.intermaps.APIParsers.RouteResponse

interface ORSAPI {
    @GET("geocode/reverse")
    fun getToponym(
        @Query("api_key") apiKey: String,
        @Query("point.lon", encoded = true) longitude: Double,
        @Query("point.lat", encoded = true) latitude: Double
    ): Call<CoordToToponymORSAPIResponse>


    @GET("geocode/search")
    suspend fun getCoordinatesFromToponym(
        @Query("api_key") apiKey: String,
        @Query("text") toponym: String
    ):ToponymToCoordORSAPIResponse

    @GET("v2/directions/driving-car")
    suspend fun calculateRoute(
        @Query("api_key") apiKey: String,
        @Query("start", encoded = true) origin: String,
        @Query("end", encoded = true) destination: String
    ):Response<RouteResponse>
}