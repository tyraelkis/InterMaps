package uji.es.intermaps.Model.Strategy

import uji.es.intermaps.Interfaces.CostStrategy
import uji.es.intermaps.Model.Route
import java.math.BigDecimal
import java.math.RoundingMode

class WalkingCostStrategy : CostStrategy {
    override suspend fun calculateCost(route: Route): Double {
        val caloriasMediaCaminar = 62
        val coste = route.distance * caloriasMediaCaminar
        return BigDecimal(coste).setScale(1, RoundingMode.HALF_UP).toDouble()
    }
}