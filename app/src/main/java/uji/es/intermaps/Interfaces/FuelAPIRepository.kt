package uji.es.intermaps.Interfaces

interface FuelAPIRepository {
    suspend fun getRegionID(region: String): String

}