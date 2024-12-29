package uji.es.intermaps.Model.Strategy

import uji.es.intermaps.Interfaces.CostStrategy
import uji.es.intermaps.Model.Route
import uji.es.intermaps.ViewModel.RouteService
import java.math.BigDecimal
import java.math.RoundingMode

class ElectricCostStrategy(
    private val routeService: RouteService,
    private val consumo: Double
) : CostStrategy {
    override suspend fun calculateCost(route: Route): Double {
        val coste = (route.distance / 100) * consumo * (routeService.getElctricCost() / 1000)
        return BigDecimal(coste).setScale(3, RoundingMode.HALF_UP).toDouble()
    }
}