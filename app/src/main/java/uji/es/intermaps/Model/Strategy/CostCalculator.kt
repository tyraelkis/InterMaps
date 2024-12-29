package uji.es.intermaps.Model.Strategy

import uji.es.intermaps.Interfaces.CostStrategy
import uji.es.intermaps.Model.Route

class CostCalculator(
    private val strategy: CostStrategy
) {
    suspend fun calculate(route: Route): Double {
        return strategy.calculateCost(route)
    }
}