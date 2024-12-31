package uji.es.intermaps.Model

import android.os.Build
import androidx.annotation.RequiresApi
import uji.es.intermaps.Interfaces.ProxyService
import java.time.LocalDate
import java.time.LocalDateTime

class CachePrecioLuz( val service: ProxyService ): ProxyService {
    private var precio: Double = 0.0
    private var dia: LocalDate? = null
    private var hora: Int? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getLightPrice(): Double {
        if (dia != LocalDate.now() && hora != LocalDateTime.now().hour) {
            precio = service.getLightPrice()
            dia = LocalDate.now()
            hora = LocalDateTime.now().hour
        }
        return precio
    }
}