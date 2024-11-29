package uji.es.intermaps.Model

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uji.es.intermaps.Interfaces.ORSAPI
import uji.es.intermaps.Interfaces.PrecioCarburanteAPI
import uji.es.intermaps.Interfaces.PrecioLuzAPI

object RetrofitConfig {
    fun createRetrofitOpenRouteService(): ORSAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .addConverterFactory(GsonConverterFactory.create())
            //.addCallAdapterFactory()
            .build()
        return retrofit.create(ORSAPI::class.java)
    }

    fun createRetrofitPrecioLuz(): PrecioLuzAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.preciodelaluz.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PrecioLuzAPI::class.java)
    }

    fun createRetrofitPrecioCarburante(): PrecioCarburanteAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(PrecioCarburanteAPI::class.java)
    }
}