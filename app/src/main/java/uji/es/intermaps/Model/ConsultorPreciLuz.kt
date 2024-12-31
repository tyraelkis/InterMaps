package uji.es.intermaps.Model

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import uji.es.intermaps.Interfaces.ProxyService
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ConsultorPreciLuz: ProxyService {
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getLightPrice(): Double {
        Log.e("CCCCCCCCCC","SE HACE LA CONSULTA CORRECTAMENTE")
        val luzAPI = RetrofitConfig.createRetrofitPrecioLuz()
        try {
            val dateformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val jsonFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")

            val now = LocalDateTime.now()
            val currentHour = now.hour

            val today = LocalDate.now()
            val startDate = today.atStartOfDay().format(dateformatter)
            val endDate = today.atTime(23, 59).format(dateformatter)

            val response = luzAPI.getElectricityCost(startDate, endDate, "hour")

            val pvpctData = response.included
                .filter { it.type == "PVPC" }
                .flatMap { it.attributes.values }

            val matchingEntry = pvpctData.firstOrNull { entry ->
                val entryHour = LocalDateTime.parse(entry.datetime, jsonFormatter).hour
                entryHour == currentHour
            }

            matchingEntry?.let {
                Log.e("AAAAAAAAAA","TODO CORRECTO :)")
                return it.value
            } ?: throw Exception("Error al obtener el precio de luz")

        } catch (e: Exception) {
            throw Exception("Error al obtener el precio de luz: ${e.message}")
        }
    }
}