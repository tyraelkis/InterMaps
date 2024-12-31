package uji.es.intermaps.Interfaces

import retrofit2.http.GET
import uji.es.intermaps.APIParsers.FuelCostAverageORSAPIResponse

interface FuelPriceAPI {

    @GET("EstacionesTerrestres")
    suspend fun getFuelCostAverage(): FuelCostAverageORSAPIResponse
}