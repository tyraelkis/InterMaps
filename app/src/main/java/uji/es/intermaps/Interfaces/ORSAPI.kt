package uji.es.intermaps.Interfaces

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import uji.es.intermaps.APIParsers.CoordToToponymORSAPIResponse

interface ORSAPI {
    @GET("geocode/reverse")
    fun getToponym(
        @Query("api_key") apiKey: String,
        @Query("point.lon", encoded = true) longitude: Double,
        @Query("point.lat", encoded = true) latitude: Double
    ): Call<CoordToToponymORSAPIResponse>

}