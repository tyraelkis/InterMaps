package uji.es.intermaps.Interfaces

import uji.es.intermaps.Model.Route

interface CostStrategy {
    suspend fun calculateCost(route: Route): Double
}