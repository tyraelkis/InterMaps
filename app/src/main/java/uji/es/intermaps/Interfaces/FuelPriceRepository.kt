package uji.es.intermaps.Interfaces


interface FuelPriceRepository {
    suspend fun calculateFuelCostAverage(): Boolean
}