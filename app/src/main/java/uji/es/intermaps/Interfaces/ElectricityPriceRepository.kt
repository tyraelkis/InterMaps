package uji.es.intermaps.Interfaces

import uji.es.intermaps.APIParsers.FuelStation
import uji.es.intermaps.Model.Coordinate
import uji.es.intermaps.Model.Route

interface ElectricityPriceRepository {
    suspend fun calculateElectricityCost(): Boolean
}