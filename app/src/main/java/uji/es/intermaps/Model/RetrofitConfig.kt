package uji.es.intermaps.Model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uji.es.intermaps.Interfaces.ORSAPI
import uji.es.intermaps.Interfaces.PrecioLuzAPI
import okhttp3.logging.HttpLoggingInterceptor
import uji.es.intermaps.Interfaces.FuelPriceAPI
import java.util.concurrent.TimeUnit


object RetrofitConfig {
    fun createRetrofitOpenRouteService(): ORSAPI {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Cambia a NONE en producción
        }

        // Configuración del cliente HTTP
        val httpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS) // Timeout para conexión
            .readTimeout(30, TimeUnit.SECONDS)    // Timeout para lectura
            .writeTimeout(30, TimeUnit.SECONDS)   // Timeout para escritura
            .build()

        // Construcción de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openrouteservice.org/")
            .client(httpClient) // Cliente HTTP con configuración personalizada
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Creación del servicio
        return retrofit.create(ORSAPI::class.java)
    }

    fun createRetrofitPrecioLuz(): PrecioLuzAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://apidatos.ree.es/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(PrecioLuzAPI::class.java)
    }

     fun createRetrofitFuelPrice(): FuelPriceAPI {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://sedeaplicaciones.minetur.gob.es/ServiciosRESTCarburantes/PreciosCarburantes/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(FuelPriceAPI::class.java)

    }
}