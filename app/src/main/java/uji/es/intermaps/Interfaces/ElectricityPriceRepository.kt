package uji.es.intermaps.Interfaces


interface ElectricityPriceRepository {
    suspend fun calculateElectricityCost(): Boolean
}