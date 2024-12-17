package uji.es.intermaps.APIParsers

import com.google.gson.annotations.SerializedName

data class FuelCostAverageORSAPIResponse(
    @SerializedName("ListaEESSPrecio") val listaEstaciones: List<FuelStation>)

data class FuelStation(
    @SerializedName("Provincia") val provincia: String?,
    @SerializedName("Precio Gasolina 95 E5") val gasolina95: String?,
    @SerializedName("Precio Gasoleo A") val gasoleoA: String?
)


