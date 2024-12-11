package uji.es.intermaps.Interfaces

import retrofit2.http.GET
import retrofit2.http.Query
import uji.es.intermaps.APIParsers.RegionORSAPIResponse


interface FuelPriceAPI {

    @GET("Listados/Provincias/")
    suspend fun getRegionID(
        @Query("text") region: String
    ): RegionORSAPIResponse

}