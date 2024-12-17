package uji.es.intermaps.Interfaces

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call
import uji.es.intermaps.APIParsers.ElectricityCostAverageORSAPIResponse

interface PrecioLuzAPI {

    @GET("es/datos/mercados/precios-mercados-tiempo-real")
    suspend fun getElectricityCost(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("time_trunc") timeTrunc: String
    ): ElectricityCostAverageORSAPIResponse
}

//https://apidatos.ree.es/es/datos/mercados/precios-mercados-tiempo-real?start_date=13-12-2024T00:00&end_date=13-12-2024T23:59&time_trunc=hour